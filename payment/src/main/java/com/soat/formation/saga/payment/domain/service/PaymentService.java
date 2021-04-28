package com.soat.formation.saga.payment.domain.service;

import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.commands.CancelPayment;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;
import com.soat.formation.saga.messages.application.events.PaymentCancelled;
import com.soat.formation.saga.messages.application.events.PaymentCreated;
import com.soat.formation.saga.messages.application.events.PaymentRefused;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;
import com.soat.formation.saga.payment.application.exception.ImpossibleAddPaymentException;
import com.soat.formation.saga.payment.config.KafkaPaymentAcceptedProducer;
import com.soat.formation.saga.payment.config.KafkaPaymentCancelledProducer;
import com.soat.formation.saga.payment.config.KafkaPaymentCreatedProducer;
import com.soat.formation.saga.payment.config.KafkaPaymentRefusedProducer;
import com.soat.formation.saga.payment.domain.model.Payment;
import com.soat.formation.saga.payment.infra.dao.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaPaymentCreatedProducer<PaymentCreated> kafkaPaymentCreatedProducer;
    @Autowired
    private KafkaPaymentAcceptedProducer<PaymentAccepted> kafkaPaymentAcceptedProducer;
    @Autowired
    private KafkaPaymentRefusedProducer<PaymentRefused> kafkaPaymentRefusedProducer;
    @Autowired
    private KafkaPaymentCancelledProducer<PaymentCancelled> kafkaPaymentCancelledProducer;


    @KafkaListener(topics = "order", groupId = "payment-orderRegistered", containerFactory="orderRegisteredKafkaListenerContainerFactory")
    @Transactional
    public void consume(OrderRegistered orderRegistered) {
        LOGGER.info(String.format("Consumed orderRegistered %s", orderRegistered));
        try {
            Payment payment = createPayment(orderRegistered);
            UUID transactionId = orderRegistered.getTransactionId();
            PaymentCreated paymentCreated = new PaymentCreated(transactionId, payment.getAmount());
            LOGGER.info(String.format("************ Sending paymentCreated with event id %s", transactionId));
            kafkaPaymentCreatedProducer.send(paymentCreated);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment");
            throw new ImpossibleAddPaymentException("Impossible d'ajouter ce payment");
        }
    }

    @KafkaListener(topics = "order", groupId = "payment-orderCreated", containerFactory="orderCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consume(OrderCreated orderCreated) {
        LOGGER.info(String.format("DO NOTHING : Consumed OrderCreated %s", orderCreated));
        //  DO NOTHING
    }

    @KafkaListener(topics = "payment", groupId = "payment-acceptPayment", containerFactory="acceptPaymentKafkaListenerContainerFactory")
    @Transactional
    public void consume(AcceptPayment acceptPayment) {
        LOGGER.info(String.format("Consumed AcceptPayment %s", acceptPayment));
        try {
            UUID transactionId = acceptPayment.getTransactionId();
            Payment payment = paymentRepository.findOneByTransactionId(transactionId.toString());

            if(payment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce paiment n'a pas été trouvé");
            }
            payment.accept();
            paymentRepository.save(payment);
            PaymentAccepted paymentAccepted = new PaymentAccepted(transactionId, payment.getAmount());
            LOGGER.info(String.format("************ Sending paymentAccepted with event id %s", transactionId));
            kafkaPaymentAcceptedProducer.send(paymentAccepted);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment");
            throw new ImpossibleAddPaymentException("Impossible d'ajouter ce payment");
        }
    }

    @KafkaListener(topics = "payment", groupId = "payment-cancelPayment", containerFactory="cancelPaymentKafkaListenerContainerFactory")
    @Transactional
    public void consume(CancelPayment cancelPayment) {
        LOGGER.info(String.format("Consumed AcceptPayment %s", cancelPayment));
        try {
            UUID transactionId = cancelPayment.getTransactionId();
            Payment payment = paymentRepository.findOneByTransactionId(transactionId.toString());

            if(payment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce paiment n'a pas été trouvé");
            }
            paymentRepository.delete(payment);
            PaymentCancelled paymentCancelled = new PaymentCancelled(transactionId, payment.getAmount());
            LOGGER.info(String.format("************ Sending paymentCancelled with event id %s", transactionId));
            kafkaPaymentCancelledProducer.send(paymentCancelled);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment");
            throw new ImpossibleAddPaymentException("Impossible d'ajouter ce payment");
        }
    }

    @KafkaListener(topics = "payment", groupId = "payment-refusePayment", containerFactory="refusePaymentKafkaListenerContainerFactory")
    @Transactional
    public void consume(RefusePayment refusePayment) {
        LOGGER.info(String.format("Consumed RefusePayment %s ", refusePayment));
        try {
            UUID transactionId = refusePayment.getTransactionId();
            Payment payment = paymentRepository.findOneByTransactionId(transactionId.toString());
            if(payment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce paiment n'a pas été trouvé");
            }
            payment.refuse();
            paymentRepository.save(payment);
            PaymentRefused paymentRefused = new PaymentRefused(transactionId, payment.getQuantity(), payment.getAmount());
            LOGGER.info(String.format("************* Sending paymentRefused with event id %s", transactionId));
            kafkaPaymentRefusedProducer.send(paymentRefused);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment");
            throw new ImpossibleAddPaymentException("Impossible d'ajouter ce payment");
        }
    }

    @KafkaListener(topics = "payment", groupId = "payment-stockBookingFailed", containerFactory="stockBookingFailedListenerContainerFactory")
    @Transactional
    public void consume(StockBookingFailed stockBookingFailed) {
        LOGGER.info(String.format("Consumed RefusePayment %s ", stockBookingFailed));
        try {
            UUID transactionId = stockBookingFailed.getTransactionId();
            Payment payment = paymentRepository.findOneByTransactionId(transactionId.toString());
            if(payment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce paiment n'a pas été trouvé");
            }
            paymentRepository.delete(payment);
            PaymentCancelled paymentCancelled = new PaymentCancelled(transactionId, payment.getAmount());
            LOGGER.info(String.format("************* Sending paymentCancelled with event id %s", transactionId));
            kafkaPaymentCancelledProducer.send(paymentCancelled);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment");
            throw new ImpossibleAddPaymentException("Impossible d'ajouter ce payment");
        }
    }

    @KafkaListener(topics = "stock", groupId = "payment-stockBooked", containerFactory="stockBookedListenerContainerFactory")
    @Transactional
    public void consume(StockBooked stockBooked) {
        LOGGER.info(String.format("Consumed stockBooked %s", stockBooked));
        try {
            UUID transactionId = stockBooked.getTransactionId();
            Payment payment = paymentRepository.findOneByTransactionId(transactionId.toString());
            if(payment == null) {
                throw new ImpossibleAddPaymentException(String.format("Impossible d'ajouter une livraison pour la transaction %s", transactionId.toString()));
            }
            payment.setStockBooked(true);
            paymentRepository.save(payment);

        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddPaymentException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @KafkaListener(topics = "payment", groupId = "payment-paymentAccepted", containerFactory="paymentAcceptedKafkaListenerContainerFactory")
    @Transactional
    public void consume(PaymentAccepted paymentAccepted) {
        LOGGER.info("Consumed PaymentAccepted from Payment");
    }

    @KafkaListener(topics = "payment", groupId = "payment-paymentCreated", containerFactory="paymentCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consume(PaymentCreated paymentCreated) {
        LOGGER.info("Consumed PaymentCreated from Payment");
    }

    @KafkaListener(topics = "payment", groupId = "payment-paymentRefused", containerFactory="paymentRefusedKafkaListenerContainerFactory")
    @Transactional
    public void consume(PaymentRefused paymentRefused) {
        LOGGER.info("Consumed PaymentRefused from Payment");
    }

    @Transactional
    public String save(Payment payment) {
        LOGGER.info("saving Initial Payment " + payment);
        paymentRepository.save(payment);
        return payment.getTransactionId();
    }

    private Payment createPayment(OrderRegistered orderRegistered) {
        UUID transactionId = orderRegistered.getTransactionId();
        int priceUnit = 5;
        float amount = Integer.valueOf(orderRegistered.getQuantity() * priceUnit).floatValue();
        Payment payment = new Payment(transactionId.toString(), orderRegistered.getQuantity(), amount);
        paymentRepository.save(payment);
        return payment;
    }
}

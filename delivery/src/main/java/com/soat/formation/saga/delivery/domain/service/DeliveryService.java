package com.soat.formation.saga.delivery.domain.service;

import com.soat.formation.saga.delivery.application.exception.ImpossibleAddDeliveryException;
import com.soat.formation.saga.delivery.config.KafkaDeliveryCanceledProducer;
import com.soat.formation.saga.delivery.config.KafkaDeliveryStartedProducer;
import com.soat.formation.saga.delivery.domain.model.Delivery;
import com.soat.formation.saga.delivery.infra.dao.DeliveryRepository;
import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.BillingCompleted;
import com.soat.formation.saga.messages.application.events.DeliveryCancelled;
import com.soat.formation.saga.messages.application.events.DeliveryStarted;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;
import com.soat.formation.saga.messages.application.events.PaymentCreated;
import com.soat.formation.saga.messages.application.events.PaymentRefused;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.transaction.Transactional;

@Service
public class DeliveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryService.class);

    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private KafkaDeliveryStartedProducer<DeliveryStarted> kafkaDeliveryStartedProducer;
    @Autowired
    private KafkaDeliveryCanceledProducer<DeliveryCancelled> kafkaDeliveryCanceledProducer;

    @KafkaListener(topics = "payment", groupId = "delivery-paymentAccepted", containerFactory="paymentAcceptedListenerContainerFactory")
    @Transactional
    public void consume(PaymentAccepted paymentAccepted) {
        LOGGER.info(String.format("Consumed paymentAccepted %s", paymentAccepted));
        try {
            UUID transactionId = paymentAccepted.getTransactionId();
            Delivery delivery = deliveryRepository.findByTransactionId(transactionId.toString());
            if(delivery == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette livraison n'a pas été trouvée");
            }
            delivery.paymentAccepted();
            delivery.setAmount(paymentAccepted.getAmount());

            boolean shouldSendIsShippedMessage = false;

            if(delivery.isBillingCompleted() && delivery.isPaymentAccepted() && delivery.isStockBooked()) {
                LOGGER.info(String.format("Delivery %s can be shipped", transactionId));
                delivery.shipped();
                shouldSendIsShippedMessage = true;
            }
            deliveryRepository.save(delivery);
            LOGGER.info(String.format("Delivery %s update", delivery));

            if(shouldSendIsShippedMessage) {
                DeliveryStarted deliveryStarted = new DeliveryStarted(transactionId);
                LOGGER.info(String.format("Sending deliveryStarted with event id %s", transactionId));
                kafkaDeliveryStartedProducer.send(deliveryStarted).get();
            }
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddDeliveryException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @KafkaListener(topics = "payment", groupId = "delivery-acceptPayment", containerFactory="acceptPaymentListenerContainerFactory")
    @Transactional
    public void consume(AcceptPayment acceptPayment) {
        LOGGER.info("Consumed AcceptPayment from DELIVERY service");
        // TO NOTHING
    }

    @KafkaListener(topics = "payment", groupId = "delivery-refusePayment", containerFactory="refusePaymentListenerContainerFactory")
    @Transactional
    public void consume(RefusePayment refusePayment) {
        LOGGER.info("Consumed RefusePayment from DELIVERY service");
        //  NOTHING
    }

    @KafkaListener(topics = "payment", groupId = "delivery-paymentCreated", containerFactory="paymentCreatedListenerContainerFactory")
    @Transactional
    public void consume(PaymentCreated paymentCreated) {
        LOGGER.info("Consumed PaymentCreated from DELIVERY service");
        // NOTHING
    }

    @KafkaListener(topics = "payment", groupId = "delivery-paymentRefused", containerFactory="paymentRefusedListenerContainerFactory")
    @Transactional
    public void consume(PaymentRefused paymentRefused) throws ExecutionException, InterruptedException {
        LOGGER.info("Consumed PaymentRefused from DELIVERY service");
        // TO NOTHING
        UUID transactionId = paymentRefused.getTransactionId();
        Delivery delivery = deliveryRepository.findByTransactionId(transactionId.toString());
        if(delivery == null) {
            LOGGER.error("Cette livraison n'a pas été trouvée");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette livraison n'a pas été trouvée");
        }
        List<Delivery> deliveries = deliveryRepository.findAll();
        LOGGER.info(String.format("La facture suivante va etre supprimée %s", delivery));
        deliveries.remove(delivery);
        LOGGER.info(String.format("Sending deliveryCanceled with transaction id %s", transactionId));
        DeliveryCancelled deliveryCancelled = new DeliveryCancelled(transactionId);
        kafkaDeliveryCanceledProducer.send(deliveryCancelled).get();

    }

    @KafkaListener(topics = "billing", groupId = "delivery-billingCompleted", containerFactory="billingCompletedListenerContainerFactory")
    @Transactional
    public void consume(BillingCompleted billingCompleted) {
        LOGGER.info(String.format("Consumed billingCompleted %s", billingCompleted));
        try {
            UUID transactionId = billingCompleted.getTransactionId();
            Delivery delivery = deliveryRepository.findByTransactionId(transactionId.toString());
            if(delivery == null) {
                LOGGER.info("Delivery was deleted because Not enough Stock left");
                return;
                //throw new ImpossibleAddDeliveryException(String.format("Impossible d'ajouter une livraison pour la transaction %s", transactionId.toString()));
            }
            delivery.billingCompleted();
            delivery.setAmount(billingCompleted.getAmount());
            boolean shouldSendIsShippedMessage = false;

            if(delivery.isBillingCompleted() && delivery.isPaymentAccepted() && delivery.isStockBooked()) {
                LOGGER.info(String.format("Delivery %s can be shipped", transactionId));
                delivery.shipped();
                shouldSendIsShippedMessage = true;
            }
            deliveryRepository.save(delivery);
            LOGGER.info(String.format("Delivery %s update", delivery));

            if(shouldSendIsShippedMessage) {
                DeliveryStarted deliveryStarted = new DeliveryStarted(transactionId);
                LOGGER.info(String.format("Sending deliveryStarted with event id %s", transactionId));
                kafkaDeliveryStartedProducer.send(deliveryStarted).get();
            }
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment " + ex);
            throw new ImpossibleAddDeliveryException("Impossible d'ajouter cette facture" + ex);
        }
    }


    @KafkaListener(topics = "stock", groupId = "delivery-stockBooked", containerFactory="stockBookedListenerContainerFactory")
    @Transactional
    public void consume(StockBooked stockBooked) {
        LOGGER.info(String.format("Consumed stockBooked %s", stockBooked));
        try {
            UUID transactionId = stockBooked.getTransactionId();
            Delivery delivery = deliveryRepository.findByTransactionId(transactionId.toString());
            if(delivery == null) {
                throw new ImpossibleAddDeliveryException(String.format("Impossible d'ajouter une livraison pour la transaction %s", transactionId.toString()));
            }
            delivery.stockBooked();
            delivery.setAmount(stockBooked.getAmount());
            boolean shouldSendIsShippedMessage = false;

            if(delivery.isBillingCompleted() && delivery.isPaymentAccepted() && delivery.isStockBooked()) {
                LOGGER.info(String.format("Delivery %s can be shipped", transactionId));
                delivery.shipped();
                shouldSendIsShippedMessage = true;
            }
            deliveryRepository.save(delivery);
            LOGGER.info(String.format("Delivery %s update", delivery));

            if(shouldSendIsShippedMessage) {
                DeliveryStarted deliveryStarted = new DeliveryStarted(transactionId);
                LOGGER.info(String.format("Sending deliveryStarted with event id %s", transactionId));
                kafkaDeliveryStartedProducer.send(deliveryStarted).get();
            }
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddDeliveryException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @KafkaListener(topics = "stock", groupId = "delivery-stockBookingFailed", containerFactory="stockBookingFailedListenerContainerFactory")
    @Transactional
    public void consume(StockBookingFailed stockBookingFailed) {
        LOGGER.info(String.format("Consumed stockBookingFailed %s", stockBookingFailed));
        try {
            UUID transactionId = stockBookingFailed.getTransactionId();
            Delivery delivery = deliveryRepository.findByTransactionId(transactionId.toString());
            if(delivery == null) {
                throw new ImpossibleAddDeliveryException(String.format("Impossible d'ajouter une livraison pour la transaction %s", transactionId.toString()));
            }
            LOGGER.info(String.format("Removing delivery with transactionId %s", transactionId));
            deliveryRepository.delete(delivery);
            LOGGER.info(String.format("Delivery %s deleted", transactionId));
            DeliveryCancelled deliveryCancelled = new DeliveryCancelled(transactionId);
            LOGGER.info(String.format("Sending deliveryCanceled with event id %s", transactionId));
            kafkaDeliveryCanceledProducer.send(deliveryCancelled).get();
        }
        catch(Exception ex) {
            LOGGER.error("StockBookingFailed " + ex);
            throw new ImpossibleAddDeliveryException("StockBookingFailed " + ex);
        }
    }

    @Transactional
    @KafkaListener(topics = "order", groupId = "delivery-orderRegistered", containerFactory="orderRegisteredListenerContainerFactory")
    public void consume(OrderRegistered orderRegistered) {
        LOGGER.info(String.format("$$$$ => Consumed message: %s DELIVERY PREPARED NOT USED", orderRegistered));
        try {
            Delivery delivery = new Delivery(orderRegistered.getTransactionId(), orderRegistered.getAddress(),
                                             orderRegistered.getQuantity());
            deliveryRepository.save(delivery);
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter cette commande" + ex);
            throw new ImpossibleAddDeliveryException("Impossible d'ajouter cette commande" + ex);
        }
    }
}

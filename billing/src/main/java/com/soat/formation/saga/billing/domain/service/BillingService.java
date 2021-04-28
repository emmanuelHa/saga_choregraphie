package com.soat.formation.saga.billing.domain.service;

import com.soat.formation.saga.billing.application.exception.ImpossibleAddBillingException;
import com.soat.formation.saga.billing.config.KafkaBillingCancelledProducer;
import com.soat.formation.saga.billing.config.KafkaBillingCompletedProducer;
import com.soat.formation.saga.billing.domain.model.Billing;
import com.soat.formation.saga.billing.infra.dao.BillingRepository;
import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.BillingCancelled;
import com.soat.formation.saga.messages.application.events.BillingCompleted;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;
import com.soat.formation.saga.messages.application.events.PaymentCancelled;
import com.soat.formation.saga.messages.application.events.PaymentCreated;
import com.soat.formation.saga.messages.application.events.PaymentRefused;

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
public class BillingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingService.class);

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private KafkaBillingCompletedProducer<BillingCompleted> kafkaBillingCompletedProducer;

    @Autowired
    private KafkaBillingCancelledProducer<BillingCancelled> kafkaBillingCancelledProducer;

    @KafkaListener(topics = "payment", groupId = "billing-paymentAccepted", containerFactory="paymentAcceptedListenerContainerFactory")
    @Transactional
    public void consume(PaymentAccepted paymentAccepted) {
        LOGGER.info(String.format("Consumed paymentAccepted %s ", paymentAccepted));
        try {
            UUID transactionId = paymentAccepted.getTransactionId();
            Billing billing = billingRepository.findByTransactionId(transactionId.toString());
            if(billing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentAccepted Cette facture n'a pas été trouvée");
            }
            billing.isBeingPaid();
            billingRepository.save(billing);
            BillingCompleted billingCompleted = new BillingCompleted(transactionId, paymentAccepted.getAmount());
            LOGGER.info(String.format("Sending billingCompleted with event id %s", transactionId));
            kafkaBillingCompletedProducer.send(billingCompleted).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddBillingException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @KafkaListener(topics = "payment", groupId = "billing-paymentCancelled", containerFactory="paymentCancelledListenerContainerFactory")
    @Transactional
    public void consume(PaymentCancelled paymentCancelled) {
        LOGGER.info(String.format("Consumed paymentCancelled %s", paymentCancelled));
        try {
            UUID transactionId = paymentCancelled.getTransactionId();
            Billing billing = billingRepository.findByTransactionId(transactionId.toString());
            if(billing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce paiement n'a pas été trouvée");
            }
            billingRepository.delete(billing);
            BillingCancelled billingCancelled = new BillingCancelled(transactionId);
            LOGGER.info(String.format("Sending billingCancelled with event id %s", transactionId));
            kafkaBillingCancelledProducer.send(billingCancelled).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddBillingException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @Transactional
    @KafkaListener(topics = "order", groupId = "billing-orderRegistered", containerFactory="orderRegisteredListenerContainerFactory")
    public void consume(OrderRegistered orderRegistered) {
        LOGGER.info(String.format("Consumed orderRegistered %s", orderRegistered));
        try {
            UUID transactionId = orderRegistered.getTransactionId();
            String address = orderRegistered.getAddress();
            Integer quantity = orderRegistered.getQuantity();
            float priceUnit = 5;
            Billing billing = new Billing(transactionId.toString(), address, quantity, quantity * priceUnit);
            billingRepository.save(billing);
            //BillingCompleted billingCompleted = new BillingCompleted(transactionId);
            //LOGGER.info(String.format("Sending billingCompleted with event id %s", transactionId));
            //kafkaBillingCompletedProducer.send(billingCompleted).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddBillingException("Impossible d'ajouter cette facture" + ex);
        }
    }

    @KafkaListener(topics = "payment", groupId = "billing-paymentCreated", containerFactory="paymentCreatedListenerContainerFactory")
    @Transactional
    public void consume(PaymentCreated paymentCreated) {
        LOGGER.info(String.format("Consumed paymentCreated %s", paymentCreated));
        try {
            UUID transactionId = paymentCreated.getTransactionId();
            Billing billing = billingRepository.findByTransactionId(transactionId.toString());
            if(billing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentCreated Cette facture n'a pas été trouvée");
            }
            billingRepository.save(billing);
            BillingCompleted billingCompleted = new BillingCompleted(transactionId, billing.getAmount());
            LOGGER.info(String.format("Sending billingCompleted with event id %s", transactionId));
            // TODO check if no more stock
            kafkaBillingCompletedProducer.send(billingCompleted).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter ce payment" + ex);
            throw new ImpossibleAddBillingException("Impossible d'ajouter cette facture " + ex);
        }
    }

    @KafkaListener(topics = "payment", groupId = "billing-acceptPayment", containerFactory="acceptPaymentListenerContainerFactory")
    @Transactional
    public void consume(AcceptPayment acceptPayment) {
        LOGGER.info("DO NOTHING AcceptPayment from BILLING");
    }

    @KafkaListener(topics = "payment", groupId = "billing-refusePayment", containerFactory="refusePaymentListenerContainerFactory")
    @Transactional
    public void consume(RefusePayment refusePayment) {
        LOGGER.info("DO NOTHING RefusePayment from BILLING");
    }

    @KafkaListener(topics = "payment", groupId = "billing-paymentRefused", containerFactory="paymentRefusedListenerContainerFactory")
    @Transactional
    public void consume(PaymentRefused paymentRefused) throws ExecutionException, InterruptedException {
        LOGGER.info(String.format("Consumed PaymentRefused %s", paymentRefused));
        // TODO BillingCancelled
        UUID transactionId = paymentRefused.getTransactionId();
        Billing billing = billingRepository.findByTransactionId(transactionId.toString());
        if(billing == null) {
            LOGGER.error("Cette facture n'a pas été trouvée" + paymentRefused);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentRefused cette facture n'a pas été trouvée");
        }
        List<Billing> billings = billingRepository.findAll();
        LOGGER.info(String.format("La facture suivante va etre supprimée %s", billing));
        billings.remove(billing);
        LOGGER.info(String.format("Sending billingCompleted with event id %s", transactionId));
        BillingCancelled billingCancelled = new BillingCancelled(transactionId);
        kafkaBillingCancelledProducer.send(billingCancelled).get();
    }
}

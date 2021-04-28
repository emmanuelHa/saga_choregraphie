package com.soat.formation.saga.payment.config;

import static org.junit.jupiter.api.Assertions.*;

import com.soat.formation.saga.messages.application.events.PaymentCreated;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

class KafkaPaymentCreatedProducerTest {

    @Test
    void topicByEventType() {
        KafkaPaymentCreatedProducer<PaymentCreated> kafkaPaymentCreatedProducer = new KafkaPaymentCreatedProducer<PaymentCreated>();
        ParameterizedType parameterizedType = (ParameterizedType) kafkaPaymentCreatedProducer.getClass().getGenericSuperclass();
        System.out.println("parameterizedType = " + parameterizedType.getTypeName());
        System.out.println("parameterizedType = " + parameterizedType.getActualTypeArguments()[0].getTypeName());
    }
}

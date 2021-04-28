package com.soat.formation.saga.payment.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.PaymentCreated;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.Map;

@Configuration
public class KafkaPaymentCreatedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaPaymentCreatedProducer<T> newKafkaPaymentCreatedProducer() {
        return new KafkaPaymentCreatedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {


        return Map.of(PaymentCreated.class, "payment");
    }




}

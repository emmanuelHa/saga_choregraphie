package com.soat.formation.saga.payment.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaPaymentAcceptedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaPaymentAcceptedProducer<T> newKafkaPaymentAcceptedProducer() {
        return new KafkaPaymentAcceptedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(PaymentAccepted.class, "payment");
    }

}

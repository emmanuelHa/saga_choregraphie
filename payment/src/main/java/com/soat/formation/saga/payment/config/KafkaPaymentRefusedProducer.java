package com.soat.formation.saga.payment.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.PaymentRefused;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaPaymentRefusedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaPaymentRefusedProducer<T> newKafkaPaymentRefusedProducer() {
        return new KafkaPaymentRefusedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(PaymentRefused.class, "payment");
    }

}

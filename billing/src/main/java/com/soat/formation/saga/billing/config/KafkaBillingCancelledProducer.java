package com.soat.formation.saga.billing.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.BillingCancelled;
import com.soat.formation.saga.messages.application.events.Event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaBillingCancelledProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaBillingCancelledProducer<T> newKafkaBillingCancelledProducer() {
        return new KafkaBillingCancelledProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(BillingCancelled.class, "billing");
    }

}

package com.soat.formation.saga.billing.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.BillingCompleted;
import com.soat.formation.saga.messages.application.events.Event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaBillingCompletedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaBillingCompletedProducer<T> newKafkaBillingCompletedProducer() {
        return new KafkaBillingCompletedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(BillingCompleted.class, "billing");
    }

}

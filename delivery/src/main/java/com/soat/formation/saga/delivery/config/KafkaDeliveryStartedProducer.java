package com.soat.formation.saga.delivery.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.DeliveryStarted;
import com.soat.formation.saga.messages.application.events.Event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaDeliveryStartedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaDeliveryStartedProducer<T> newKafkaDeliveryStartedProducer() {
        return new KafkaDeliveryStartedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(DeliveryStarted.class, "delivery");
    }

}

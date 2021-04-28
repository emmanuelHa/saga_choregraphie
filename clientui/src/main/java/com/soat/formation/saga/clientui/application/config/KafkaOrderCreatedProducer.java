package com.soat.formation.saga.clientui.application.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.OrderCreated;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
@Configuration
public class KafkaOrderCreatedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaOrderCreatedProducer<T> newKafkaOrderCreatedProducer() {
        return new KafkaOrderCreatedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(OrderCreated.class, "order");
    }

}

package com.soat.formation.saga.order.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.OrderRegistered;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaGenericProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaGenericProducer<T> newKafkaGenericProducer() {
        return new KafkaGenericProducer<T>();
    }


    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(OrderRegistered.class, "order");
    }

}

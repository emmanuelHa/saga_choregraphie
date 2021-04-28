package com.soat.formation.saga.stock.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;

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
        return Map.of(StockBooked.class, "stock",
                      StockBookingFailed.class, "stock");
    }
}

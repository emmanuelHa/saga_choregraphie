package com.soat.formation.saga.stock.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaStockBookingFailedProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaStockBookingFailedProducer<T> newKafkaStockBookingFailedProducer() {
        return new KafkaStockBookingFailedProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(StockBooked.class, "stock",
                      StockBookingFailed.class, "stock");
    }

}

package com.soat.formation.saga.clientui.application.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaRefusePaymentProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaRefusePaymentProducer<T> newKafkaRefusePaymentProducer() {
        return new KafkaRefusePaymentProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(RefusePayment.class, "payment",
                      PaymentAccepted.class, "payment");
    }

}

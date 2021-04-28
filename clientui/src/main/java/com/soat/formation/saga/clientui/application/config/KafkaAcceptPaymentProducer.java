package com.soat.formation.saga.clientui.application.config;

import com.soat.formation.saga.infra.config.AbstractKafkaGenericProducer;
import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.events.Event;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaAcceptPaymentProducer<T extends Event> extends AbstractKafkaGenericProducer<T> {

    @Bean
    public KafkaAcceptPaymentProducer<T> newKafkaPaymentCreatedProducer() {
        return new KafkaAcceptPaymentProducer<T>();
    }

    @Override
    public Map<Class, String> topicByEventType() {
        return Map.of(AcceptPayment.class, "payment",
                      PaymentAccepted.class, "payment");
    }

}

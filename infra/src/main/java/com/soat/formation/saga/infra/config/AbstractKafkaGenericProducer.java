package com.soat.formation.saga.infra.config;

import com.soat.formation.saga.messages.application.events.Event;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:infra.properties")
public abstract class AbstractKafkaGenericProducer<T> {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /** Used by producers
     * @return a map whose key is the class of event type (@link com.soat.formation.saga.messages.application.events.Event),
     * the value is the topic to send event to.
     */
    public abstract Map<Class, String> topicByEventType();

    public ListenableFuture<SendResult<String, T>> send(T event) {
        KafkaTemplate<String, T> stringObjectKafkaTemplate = genericKafkaTemplate();
        return stringObjectKafkaTemplate.send(getTopicByEventType(event.getClass()),
                                              event.getClass().getSimpleName(), event);
    }

    private ProducerFactory<String, T> genericProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private KafkaTemplate<String, T> genericKafkaTemplate() {
        return new KafkaTemplate<>(genericProducerFactory());
    }

    private String getTopicByEventType(Class<?> eventTypeClass) {
        return topicByEventType().get(eventTypeClass);
    }

}

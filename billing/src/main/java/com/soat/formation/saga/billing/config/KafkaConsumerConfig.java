package com.soat.formation.saga.billing.config;

import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;
import com.soat.formation.saga.messages.application.events.PaymentCancelled;
import com.soat.formation.saga.messages.application.events.PaymentCreated;
import com.soat.formation.saga.messages.application.events.PaymentRefused;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@EnableKafka
@Configuration
@ComponentScan("com.soat.formation.saga.infra.config")
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    @Qualifier("infraConsumerJsonProps")
    private Map<String, Map<String, Object>> jsonConsumerProps;


    @Bean
    public ConsumerFactory<String, OrderRegistered> consumerOrderRegisteredFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> orderRegisteredListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderRegisteredFactory());
        factory.setRecordFilterStrategy(record -> !OrderRegistered.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentAccepted> consumerPaymentAcceptedFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentAccepted> paymentAcceptedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentAccepted> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentAcceptedFactory());
        factory.setRecordFilterStrategy(record -> !PaymentAccepted.class.getSimpleName().equals(record.key()));
        return factory;
    }

    //
    @Bean
    public ConsumerFactory<String, PaymentCancelled> consumerPaymentCancelledFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCancelled> paymentCancelledListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentCancelled> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentCancelledFactory());
        factory.setRecordFilterStrategy(record -> !PaymentCancelled.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentCreated> consumerPaymentCreatedFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCreated> paymentCreatedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentCreatedFactory());
        factory.setRecordFilterStrategy(record -> !PaymentCreated.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentRefused> consumerPaymentRefusedFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> paymentRefusedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentRefusedFactory());
        factory.setRecordFilterStrategy(record -> !PaymentRefused.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, AcceptPayment> consumerAcceptPaymentFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AcceptPayment> acceptPaymentListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AcceptPayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerAcceptPaymentFactory());
        factory.setRecordFilterStrategy(record -> !AcceptPayment.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RefusePayment> consumerRefusePaymentFactory() {
        Map<String, Object> props = jsonConsumerProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "billing");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RefusePayment> refusePaymentListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, RefusePayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerRefusePaymentFactory());
        factory.setRecordFilterStrategy(record -> !RefusePayment.class.getSimpleName().equals(record.key()));

        return factory;
    }
}

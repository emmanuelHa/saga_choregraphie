package com.soat.formation.saga.payment.config;

import com.soat.formation.saga.messages.application.commands.AcceptPayment;
import com.soat.formation.saga.messages.application.commands.CancelPayment;
import com.soat.formation.saga.messages.application.commands.RefusePayment;
import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentAccepted;
import com.soat.formation.saga.messages.application.events.PaymentCreated;
import com.soat.formation.saga.messages.application.events.PaymentRefused;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;

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
public class KafkaConsumerPaymentConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    @Qualifier("infraConsumerJsonProps")
    private Map<String, Map<String, Object>> infraJsonProps;

    @Bean
    public ConsumerFactory<String, OrderRegistered> consumerFactoryOrderRegistered() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> orderRegisteredKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrderRegistered());
        factory.setRecordFilterStrategy(record -> !OrderRegistered.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderCreated> consumerFactoryOrderCreated() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreated> orderCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrderCreated());
        factory.setRecordFilterStrategy(record -> !OrderCreated.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AcceptPayment> consumerFactoryAcceptPayment() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AcceptPayment> acceptPaymentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AcceptPayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryAcceptPayment());
        factory.setRecordFilterStrategy(record -> !AcceptPayment.class.getSimpleName().equals(record.key()));
        return factory;
    }


    @Bean
    public ConsumerFactory<String, CancelPayment> consumerFactoryCancelPayment() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancelPayment> cancelPaymentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CancelPayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryCancelPayment());
        factory.setRecordFilterStrategy(record -> !CancelPayment.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RefusePayment> consumerFactoryRefusePayment() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RefusePayment> refusePaymentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, RefusePayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryRefusePayment());
        factory.setRecordFilterStrategy(record -> !RefusePayment.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentAccepted> consumerFactoryPaymentAccepted() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentAccepted> paymentAcceptedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentAccepted> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPaymentAccepted());
        factory.setRecordFilterStrategy(record -> !PaymentAccepted.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentCreated> consumerFactoryPaymentCreated() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCreated> paymentCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPaymentCreated());
        factory.setRecordFilterStrategy(record -> !PaymentCreated.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentRefused> consumerFactoryPaymentRefused() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> paymentRefusedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPaymentRefused());
        factory.setRecordFilterStrategy(record -> !PaymentRefused.class.getSimpleName().equals(record.key()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, StockBookingFailed> consumerFactoryStockBookingFailed() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockBookingFailed> stockBookingFailedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, StockBookingFailed> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryStockBookingFailed());
        factory.setRecordFilterStrategy(record -> !StockBookingFailed.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, StockBooked> consumerFactoryStockBooked() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockBooked> stockBookedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, StockBooked> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryStockBooked());
        factory.setRecordFilterStrategy(record -> !StockBooked.class.getSimpleName().equals(record.key()));

        return factory;
    }

}

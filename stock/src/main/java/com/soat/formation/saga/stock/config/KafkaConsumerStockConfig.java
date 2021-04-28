package  com.soat.formation.saga.stock.config;

import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
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
public class KafkaConsumerStockConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    @Qualifier("infraConsumerJsonProps")
    private Map<String, Map<String, Object>> infraJsonProps;

    @Bean
    public ConsumerFactory<String, OrderRegistered> consumerOrderFactory() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stock");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> orderRegisteredKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderFactory());
        factory.setRecordFilterStrategy(record -> !OrderRegistered.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderCreated> consumerOrderCreatedFactory() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stock");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreated> orderCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderCreatedFactory());
        factory.setRecordFilterStrategy(record -> !OrderCreated.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentRefused> consumerPaymentRefusedFactory() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stock");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> paymentRefusedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentRefused> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentRefusedFactory());
        factory.setRecordFilterStrategy(record -> !PaymentRefused.class.getSimpleName().equals(record.key()));

        return factory;
    }
    //

}

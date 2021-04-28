package  com.soat.formation.saga.order.config;

import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

// Useless class now ? override group id

@EnableKafka
@Configuration
@ComponentScan("com.soat.formation.saga.infra.config")
@PropertySource("classpath:infra.properties")
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    @Qualifier("infraConsumerJsonProps")
    private Map<String, Map<String, Object>> infraJsonProps;


    @Bean
    public ConsumerFactory<String, OrderCreated> consumerFactoryOrderCreated() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreated> orderCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrderCreated());
        // concurrency useless here
        //factory.setConcurrency(5);
        factory.setRecordFilterStrategy(record -> !OrderCreated.class.getSimpleName().equals(record.key()));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderRegistered> consumerOrderRegisteredFactory() {
        Map<String, Object> props = infraJsonProps.get("infraConsumerJsonProps");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> orderRegisteredKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderRegistered> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderRegisteredFactory());
        // concurrency useless here
        //factory.setConcurrency(5);
        factory.setRecordFilterStrategy(record -> !OrderRegistered.class.getSimpleName().equals(record.key()));

        return factory;
    }
}

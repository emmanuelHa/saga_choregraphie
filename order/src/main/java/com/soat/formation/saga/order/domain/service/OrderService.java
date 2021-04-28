package  com.soat.formation.saga.order.domain.service;

import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.order.application.web.exception.ImpossibleAddOrderException;
import com.soat.formation.saga.order.config.KafkaGenericProducer;
import com.soat.formation.saga.order.domain.model.Order;
import com.soat.formation.saga.order.infra.dao.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaGenericProducer<OrderRegistered> kafkaGenericProducer;

    @KafkaListener(topics = "order", groupId = "order-orderCreated", containerFactory="orderCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consume(OrderCreated orderCreated) {
        LOGGER.info(String.format("Consumed message: %s", orderCreated));
        try {
            Order order = new Order(orderCreated.getQuantity(), orderCreated.getAddress());
            order.setTransactionId(orderCreated.getTransactionId().toString());
            orderRepository.save(order);
            OrderRegistered orderRegistered = new OrderRegistered(orderCreated.getQuantity(), orderCreated.getAddress(),
                                                                  orderCreated.getTransactionId());
            LOGGER.info(String.format("Sending orderRegistered with event id %s", orderRegistered.getTransactionId()));
            kafkaGenericProducer.send(orderRegistered).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter cette command");
            throw new ImpossibleAddOrderException("Impossible d'ajouter cette commande");
        }
    }

    @KafkaListener(topics = "order", groupId = "order-orderRegistered", containerFactory="orderRegisteredKafkaListenerContainerFactory")
    public void consume(OrderRegistered orderRegistered) {
        LOGGER.info("DO NOTHING : Consumed OrderRegistered from order Service");
        // DO NOTHING
    }
}

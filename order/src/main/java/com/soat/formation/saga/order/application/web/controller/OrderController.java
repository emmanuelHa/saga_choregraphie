package  com.soat.formation.saga.order.application.web.controller;

import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.order.application.web.exception.ImpossibleAddOrderException;
import com.soat.formation.saga.order.domain.model.Order;
import com.soat.formation.saga.order.infra.dao.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

//@RestController
public class OrderController {

    private final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    //@PostMapping(value = "/publish/order")
    public ResponseEntity<Order> saveOrder(@RequestParam Integer quantity, @RequestParam String address) {
        Order newOrder = null;
        try {
            Order order = createOrder(quantity, address);
            newOrder = orderRepository.save(order);
            OrderCreated orderCreated = new OrderCreated(newOrder.getQuantity(), newOrder.getAddress());
            kafkaTemplate.send("order", OrderCreated.status.OrderCreated.name(), orderCreated).get();
        }
        catch(Exception ex) {
            LOGGER.error("Impossible d'ajouter cet order");
            throw new ImpossibleAddOrderException("Impossible d'ajouter cet order");
        }

        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    private Order createOrder(Integer quantity, String address) {
        Order order = new Order();
        order.setTransactionId(UUID.randomUUID().toString());
        order.setQuantity(quantity);
        order.setAddress(address);
        return order;
    }

}

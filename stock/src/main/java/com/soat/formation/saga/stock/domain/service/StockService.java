package  com.soat.formation.saga.stock.domain.service;

import com.soat.formation.saga.messages.application.events.OrderCreated;
import com.soat.formation.saga.messages.application.events.OrderRegistered;
import com.soat.formation.saga.messages.application.events.PaymentRefused;
import com.soat.formation.saga.messages.application.events.StockBooked;
import com.soat.formation.saga.messages.application.events.StockBookingFailed;
import com.soat.formation.saga.stock.application.web.exception.ImpossibleAddStockException;
import com.soat.formation.saga.stock.config.KafkaStockBookedProducer;
import com.soat.formation.saga.stock.config.KafkaStockBookingFailedProducer;
import com.soat.formation.saga.stock.domain.model.Stock;
import com.soat.formation.saga.stock.infra.dao.StockDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.transaction.Transactional;

@Service
public class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockDao stockDao;

    @Autowired
    private KafkaStockBookedProducer<StockBooked> kafkaStockBookedProducer;

    @Autowired
    private KafkaStockBookingFailedProducer<StockBookingFailed> kafkaStockBookingFailedProducer;

    @KafkaListener(topics = "order", groupId = "stock-orderRegistered", containerFactory = "orderRegisteredKafkaListenerContainerFactory")
    @Transactional
    public void consume(OrderRegistered orderRegistered) {
        LOGGER.info(String.format("Consumed message orderRegistered : %s", orderRegistered));
        try {

            // TODO CREATE/USE STOCK BOOKING
            List<Stock> stocks = stockDao.findAll();
            var existingStock = stocks.stream().skip(stocks.size() - 1).findFirst().orElseThrow(() -> new IllegalStateException("No Stock available"));
            Integer existingStockQuantity = existingStock.getQuantity();
            UUID transactionId = orderRegistered.getTransactionId();
            Integer quantity = orderRegistered.getQuantity();
            if (existingStockQuantity < quantity) {
                sendStockBookingFailed(transactionId);
            }
            else {
                sendStockBooked(orderRegistered, transactionId, quantity, existingStockQuantity);
            }

        }  catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new ImpossibleAddStockException("Impossible d'ajouter ce stock");
        }
    }

    private void sendStockBooked(OrderRegistered orderRegistered, UUID transactionId, Integer quantity, Integer existingStockQuantity) throws InterruptedException, ExecutionException {
        LOGGER.info(String.format("%d left in orderRegistered. Removing %d", existingStockQuantity, quantity));
        saveNewStock(quantity, existingStockQuantity, transactionId);
        int priceUnit = 5;
        float amount = Integer.valueOf(orderRegistered.getQuantity() * priceUnit).floatValue();
        StockBooked stockBooked = new StockBooked(transactionId, amount);
        LOGGER.info("SENDING stockBooked " + stockBooked);
        kafkaStockBookedProducer.send(stockBooked).get();
    }

    private void sendStockBookingFailed(UUID transactionId) throws InterruptedException, ExecutionException {
        LOGGER.info("Not enough Stock left");
        StockBookingFailed stockBookingFailed = new StockBookingFailed(transactionId);
        kafkaStockBookingFailedProducer.send(stockBookingFailed).get();
    }

    @KafkaListener(topics = "order", groupId = "stock-orderCreated", containerFactory = "orderCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consume(OrderCreated orderCreated) {
        LOGGER.info(String.format("Consumed OrderCreated : %s", orderCreated));
        // DO NOTHING
    }

    @KafkaListener(topics = "payment", groupId = "stock-paymentRefused", containerFactory="paymentRefusedListenerContainerFactory")
    @Transactional
    public void consume(PaymentRefused paymentRefused) throws ExecutionException, InterruptedException {
        LOGGER.info(String.format("Consumed PaymentRefused %s from Stock service", paymentRefused));
        UUID transactionId = paymentRefused.getTransactionId();
        Stock stock = stockDao.findByTransactionId(transactionId.toString());
        if(stock == null) {
            LOGGER.error("Ce stock n'a pas été trouvée");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette livraison n'a pas été trouvée");
        }
        // TODO CREATE AND USE STOCK BOOKING ENTITY INSTEAD
        stock.restitute(paymentRefused.getQuantity());
        LOGGER.info(String.format("Le stock vaut maintenant %s", stock.getQuantity()));
        stockDao.save(stock);
    }

    private Stock saveNewStock(Integer quantity, Integer existingStockQuantity, UUID transactionId) {
        Stock stock = new Stock();
        stock.setTransactionId(transactionId.toString());
        stock.setQuantity(existingStockQuantity - quantity);
        return stockDao.save(stock);
    }
}

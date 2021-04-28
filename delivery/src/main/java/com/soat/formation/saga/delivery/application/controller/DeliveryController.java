package com.soat.formation.saga.delivery.application.controller;

import com.soat.formation.saga.delivery.domain.model.Delivery;
import com.soat.formation.saga.delivery.infra.dao.DeliveryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeliveryController {

    private final Logger LOGGER = LoggerFactory.getLogger(DeliveryController.class);

    @Autowired
    DeliveryRepository deliveryRepository;

    @GetMapping(value = "/deliveries")
    public List<Delivery> recupererListPaiements() {
        return deliveryRepository.findAll();
    }

    /*@Autowired
    DeliveryRepository commandeDao;

    @PostMapping(value = "/orders")
    public ResponseEntity<Delivery> addDelivery(@RequestBody Delivery Delivery) {

        Delivery newDelivery = commandeDao.save(Delivery);
        if(newDelivery == null) {
            throw new ImpossibleAddDeliveryException("Impossible d'ajouter cette Delivery");
        }
        return new ResponseEntity<>(Delivery, HttpStatus.CREATED);
    }

    @GetMapping(value = "/orders/{id}")
    public Optional<Delivery> getDelivery(@PathVariable int id) {

        Optional<Delivery> Delivery = commandeDao.findById(id);

        if(Delivery.isEmpty()) throw new DeliveryNotFoundException("Cette Delivery n'existe pas");

        return Delivery;
    }*/



}

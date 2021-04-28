package com.soat.formation.saga.messages.application.events;

import java.util.Date;
import java.util.UUID;

public class OrderRegistered implements Event {

    private UUID transactionId;
    private final Date date = new Date();

    public OrderRegistered() {}

    public OrderRegistered(Integer quantity, String address, UUID transactionId) {
        this(quantity, address);
        this.transactionId = transactionId;
    }
    public OrderRegistered(Integer quantity, String address) {
        this.quantity = quantity;
        this.address = address;
    }

    public enum status {
        OrderRegistered;

    }

    private Integer quantity;
    private String address;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "OrderRegistered{" +
            "transactionId=" + transactionId +
            ", date=" + date +
            ", quantity=" + quantity +
            ", address='" + address + '\'' +
            '}';
    }
}

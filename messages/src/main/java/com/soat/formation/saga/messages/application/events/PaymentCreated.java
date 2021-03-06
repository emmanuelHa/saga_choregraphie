package com.soat.formation.saga.messages.application.events;

import java.util.Date;
import java.util.UUID;

public class PaymentCreated implements Event {

    private final Date date = new Date();
    private UUID transactionId;
    private Integer quantity;
    private Float amount;
    private String address;

    public enum status {
        PaymentCreated;
    }

    public PaymentCreated() {}

    public PaymentCreated(UUID transactionId, Float amount) {
        this.transactionId = transactionId;
        this.amount = amount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "PaymentCreated{" +
            "date=" + date +
            ", transactionId=" + transactionId +
            ", quantity=" + quantity +
            ", amount=" + amount +
            ", address='" + address + '\'' +
            '}';
    }
}

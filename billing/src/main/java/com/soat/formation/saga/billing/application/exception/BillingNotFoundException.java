package com.soat.formation.saga.billing.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BillingNotFoundException extends RuntimeException {

    public BillingNotFoundException(String message) {
        super(message);
    }
}

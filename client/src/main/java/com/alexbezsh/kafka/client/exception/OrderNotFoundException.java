package com.alexbezsh.kafka.client.exception;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("Order " + id + " not found");
    }

}

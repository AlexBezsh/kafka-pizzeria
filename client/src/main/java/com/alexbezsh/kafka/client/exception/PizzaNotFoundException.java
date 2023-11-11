package com.alexbezsh.kafka.client.exception;

public class PizzaNotFoundException extends NotFoundException {

    public PizzaNotFoundException(Long id) {
        super("Pizza " + id + " not found");
    }

}

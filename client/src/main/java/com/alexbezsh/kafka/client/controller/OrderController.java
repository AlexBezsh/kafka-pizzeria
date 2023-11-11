package com.alexbezsh.kafka.client.controller;

import com.alexbezsh.kafka.client.controller.api.OrderApi;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    public OrderDto placeOrder(OrderRequest request) {
        log.info("Placing new order: {}", request);
        return orderService.placeOrder(request);
    }

    @Override
    public OrderDto getOrder(Long id) {
        log.info("Getting order with id {}", id);
        return orderService.getOrder(id);
    }

}

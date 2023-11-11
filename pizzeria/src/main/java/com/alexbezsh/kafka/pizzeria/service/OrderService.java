package com.alexbezsh.kafka.pizzeria.service;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.pizzeria.properties.CustomKafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomKafkaProperties properties;

    public void process(OrderMessage order) {
        Long orderId = order.getId();
        log.info("Cooking started. Order id: {}", orderId);
        // cooking imitation
        try {
            Thread.sleep(properties.getCookingTimeMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Cooking finished. Order id: {}", orderId);
    }

}

package com.alexbezsh.kafka.courier.service;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
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
        log.info("Pizza delivery started. Order id: {}", orderId);
        // delivery imitation
        try {
            Thread.sleep(properties.getDeliveryTimeMs());
        } catch (InterruptedException ignored) {
        }
        log.info("Pizza delivery finished. Order id: {}", orderId);
    }

}

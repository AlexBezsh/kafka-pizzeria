package com.alexbezsh.kafka.client.listener;

import com.alexbezsh.kafka.client.service.OrderService;
import com.alexbezsh.kafka.common.model.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import static com.alexbezsh.kafka.common.utils.JsonUtils.fromJson;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusListener {

    private final OrderService orderService;

    @KafkaListener(topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "0"))
    public void partitionZeroListener(@Payload String message,
                                      @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 0 and correlation id {}", correlationId);
        processStatusUpdate(message);
    }

    @KafkaListener(topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "1"))
    public void partitionOneListener(@Payload String message,
                                     @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 1 and correlation id {}", correlationId);
        processStatusUpdate(message);
    }

    @KafkaListener(topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "2"))
    public void partitionTwoListener(@Payload String message,
                                     @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 2 and correlation id {}", correlationId);
        processStatusUpdate(message);
    }

    private void processStatusUpdate(String message) {
        orderService.updateOrderStatus(fromJson(message, OrderMessage.class));
    }

}

package com.alexbezsh.kafka.courier.listener;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderStatus;
import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.courier.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import static com.alexbezsh.kafka.common.utils.JsonUtils.fromJson;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(CustomKafkaProperties.class)
public class OrderListener {

    private final OrderService orderService;
    private final CustomKafkaProperties properties;

    @SendTo
    @KafkaListener(filter = "messageFilter", topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "0"))
    public Message<?> partitionZeroListener(@Payload String message,
                                            @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 0 and correlation id {}", correlationId);
        return processOrderMessage(message, correlationId);
    }

    @SendTo
    @KafkaListener(filter = "messageFilter", topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "1"))
    public Message<?> partitionOneListener(@Payload String message,
                                           @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 1 and correlation id {}", correlationId);
        return processOrderMessage(message, correlationId);
    }

    @SendTo
    @KafkaListener(filter = "messageFilter", topicPartitions =
    @TopicPartition(topic = "${kafka.notification-topic.name}", partitions = "2"))
    public Message<?> partitionTwoListener(@Payload String message,
                                           @Header(CORRELATION_ID) String correlationId) {
        log.info("Processing message from partition 2 and correlation id {}", correlationId);
        return processOrderMessage(message, correlationId);
    }

    private Message<?> processOrderMessage(String message, String correlationId) {
        OrderMessage order = fromJson(message, OrderMessage.class);
        orderService.process(order);
        order.setStatus(OrderStatus.DELIVERED);
        return createOrderNotification(correlationId, order);
    }

    private Message<String> createOrderNotification(String correlationId, OrderMessage order) {
        return MessageBuilder.withPayload(toJson(order))
            .setHeader(CORRELATION_ID, correlationId)
            .setHeader(properties.getMessageSourceHeader(), properties.getMessageSource())
            .build();
    }

}

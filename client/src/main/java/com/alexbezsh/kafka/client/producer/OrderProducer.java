package com.alexbezsh.kafka.client.producer;

import com.alexbezsh.kafka.client.mapper.OrderMapper;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.properties.CustomKafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final OrderMapper orderMapper;
    private final CustomKafkaProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendNewOrder(Order order) {
        String correlationId = String.valueOf(order.getId());
        String message = toJson(orderMapper.toOrderMessage(order));
        log.info("Sending new order notification. Correlation id " + correlationId);
        ProducerRecord<String, String> record =
            new ProducerRecord<>(properties.getOrderTopic().getName(), message);
        record.headers()
            .add(CORRELATION_ID, correlationId.getBytes(UTF_8))
            .add(REPLY_TOPIC, properties.getNotificationTopic().getName().getBytes(UTF_8))
            .add(properties.getMessageSourceHeader(),
                properties.getMessageSource().getBytes(UTF_8));
        kafkaTemplate.send(record);
    }

}

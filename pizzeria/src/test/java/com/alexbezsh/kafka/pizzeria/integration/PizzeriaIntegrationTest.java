package com.alexbezsh.kafka.pizzeria.integration;

import com.alexbezsh.kafka.common.KafkaIntegrationTest;
import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderStatus;
import com.alexbezsh.kafka.pizzeria.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.pizzeria.service.OrderService;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static com.alexbezsh.kafka.pizzeria.integration.TestConfig.NOTIFICATION_TOPIC_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;
import static org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps;

@Import(TestConfig.class)
public class PizzeriaIntegrationTest extends KafkaIntegrationTest {

    @SpyBean
    private OrderService orderService;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private CustomKafkaProperties customProperties;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Captor
    private ArgumentCaptor<OrderMessage> orderMessageArgumentCaptor;

    @Test
    void notificationTopicConsumerShouldReceiveOrderMessage() {
        long id = 1L;
        Consumer<String, String> consumer = createNotificationConsumer();
        OrderMessage expected = orderMessage(OrderStatus.READY);

        sendOrderMessage(id);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer,
            Duration.ofMillis(customProperties.getCookingTimeMs() + 5000));
        assertEquals(1, records.count());
        records.forEach(record -> assertEquals(toJson(expected), record.value()));

        verify(orderService, times(1)).process(orderMessageArgumentCaptor.capture());
        assertEquals(expected, orderMessageArgumentCaptor.getValue());
    }

    private Consumer<String, String> createNotificationConsumer() {
        String bootstrapServer = kafkaProperties.getBootstrapServers().get(0);
        Map<String, Object> properties = consumerProps(bootstrapServer, "test", "true");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(properties,
            StringDeserializer::new, StringDeserializer::new).createConsumer();
        consumer.subscribe(List.of(NOTIFICATION_TOPIC_NAME));
        return consumer;
    }

    private void sendOrderMessage(long id) {
        String message = toJson(orderMessage(OrderStatus.COOKING));
        byte[] correlationId = String.valueOf(id).getBytes(UTF_8);
        String orderTopic = customProperties.getOrderTopic().getName();
        ProducerRecord<String, String> record = new ProducerRecord<>(orderTopic, message);
        record.headers()
            .add(CORRELATION_ID, correlationId)
            .add(customProperties.getMessageSourceHeader(), "pizzeria".getBytes(UTF_8))
            .add(REPLY_TOPIC, NOTIFICATION_TOPIC_NAME.getBytes(UTF_8));
        template.send(record);
        template.flush();
    }

}

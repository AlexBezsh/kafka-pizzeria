package com.alexbezsh.kafka.courier.integration;

import com.alexbezsh.kafka.common.KafkaIntegrationTest;
import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderStatus;
import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.courier.service.OrderService;
import java.time.Duration;
import java.util.ArrayList;
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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;
import static org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps;

@Import(TestConfig.class)
public class CourierIntegrationTest extends KafkaIntegrationTest {

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
    void consumerWithoutFilterShouldReceiveTwoMessages() {
        Consumer<String, String> consumer = createConsumer();
        OrderMessage pizzeriaMessage = orderMessage(OrderStatus.READY);
        OrderMessage courierMessage = orderMessage(OrderStatus.DELIVERED);
        List<String> expected = List.of(toJson(pizzeriaMessage), toJson(courierMessage));

        doAnswer(invocation -> {
            assertEquals(pizzeriaMessage, invocation.getArgument(0));
            return null;
        }).when(orderService).process(any());

        sendOrderMessage();

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer,
            Duration.ofMillis(customProperties.getDeliveryTimeMs() + 5000), 2);
        List<String> actual = new ArrayList<>();
        records.iterator().forEachRemaining(record -> actual.add(record.value()));

        verify(orderService, times(1)).process(any());

        assertEquals(expected, actual);
    }

    private Consumer<String, String> createConsumer() {
        String bootstrapServer = kafkaProperties.getBootstrapServers().get(0);
        Map<String, Object> properties = consumerProps(bootstrapServer, "test", "true");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(properties,
            StringDeserializer::new, StringDeserializer::new).createConsumer();
        consumer.subscribe(List.of(customProperties.getNotificationTopic().getName()));
        return consumer;
    }

    private void sendOrderMessage() {
        byte[] correlationId = String.valueOf(1L).getBytes(UTF_8);
        String message = toJson(orderMessage(OrderStatus.READY));
        String topic = customProperties.getNotificationTopic().getName();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        record.headers().add(CORRELATION_ID, correlationId)
            .add(customProperties.getMessageSourceHeader(), "pizzeria".getBytes(UTF_8))
            .add(REPLY_TOPIC, topic.getBytes(UTF_8));
        template.send(record);
        template.flush();
    }

}

package com.alexbezsh.kafka.client.integration;

import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.client.repository.OrderRepository;
import com.alexbezsh.kafka.common.KafkaIntegrationTest;
import com.alexbezsh.kafka.common.model.OrderMessage;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static com.alexbezsh.kafka.client.TestUtils.order;
import static com.alexbezsh.kafka.client.TestUtils.orderDto;
import static com.alexbezsh.kafka.client.TestUtils.orderRequest;
import static com.alexbezsh.kafka.client.controller.OrderControllerTest.ORDERS_URL;
import static com.alexbezsh.kafka.client.controller.OrderControllerTest.ORDER_URL;
import static com.alexbezsh.kafka.common.CommonTestUtils.DATE_TIME;
import static com.alexbezsh.kafka.common.CommonTestUtils.ITEM_2_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.COOKING;
import static com.alexbezsh.kafka.common.model.OrderStatus.READY;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Sql("/test-data.sql")
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIntegrationTest extends KafkaIntegrationTest {

    @SpyBean
    private Clock clock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private CustomKafkaProperties customKafkaProperties;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Test
    void postOrderShouldReturnOrderDtoAndSendMessageToOrderTopic() throws Exception {
        OrderRequest request = orderRequest();
        Long orderId = ORDER_ID + 1;
        Long item1Id = ITEM_2_ID + 1;
        Long item2Id = ITEM_2_ID + 2;

        OrderDto expectedResponse = orderDto();
        expectedResponse.setId(orderId);
        expectedResponse.getItems().get(0).setId(item1Id);
        expectedResponse.getItems().get(1).setId(item2Id);

        OrderMessage expectedMessage = orderMessage(COOKING);
        expectedMessage.setId(orderId);
        expectedMessage.getItems().get(0).setId(item1Id);
        expectedMessage.getItems().get(1).setId(item2Id);

        Order expectedOrder = order(COOKING, DATE_TIME);
        expectedOrder.setId(orderId);
        expectedOrder.getItems().get(0).setId(item1Id);
        expectedOrder.getItems().get(1).setId(item2Id);

        Consumer<String, String> consumer = createOrderConsumer();

        mockMvc.perform(post(ORDERS_URL)
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(toJson(expectedResponse)))
            .andExpect(status().isOk());

        Order actualOrder = orderRepository.findById(orderId).get();
        assertThat(actualOrder)
            .usingRecursiveComparison()
            .ignoringFields("items.order")
            .isEqualTo(expectedOrder);

        ConsumerRecords<String, String> records =
            KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
        assertEquals(1, records.count());
        records.forEach(record -> assertEquals(toJson(expectedMessage), record.value()));
    }

    @Test
    void getOrderEndpointShouldReturnOrderDto() throws Exception {
        String expected = toJson(orderDto());

        mockMvc.perform(get(ORDER_URL))
            .andExpect(content().json(expected))
            .andExpect(status().isOk());
    }

    @Test
    void shouldProcessMessageFromNotificationTopic() throws InterruptedException {
        Duration cookingTime = Duration.ofMinutes(20);
        LocalDateTime dateTime = DATE_TIME.plus(cookingTime);
        Order expected = order(READY, dateTime);

        doReturn(dateTime.atZone(systemDefault()).toInstant()).when(clock).instant();
        doReturn(systemDefault()).when(clock).getZone();

        sendOrderNotification();

        Order actual = getOrderIfReady();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("items.order")
            .isEqualTo(expected);
    }

    private Consumer<String, String> createOrderConsumer() {
        String bootstrapServer = kafkaProperties.getBootstrapServers().get(0);
        Map<String, Object> properties = consumerProps(bootstrapServer, "test", "true");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(properties,
            StringDeserializer::new, StringDeserializer::new).createConsumer();
        String topic = customKafkaProperties.getOrderTopic().getName();
        consumer.subscribe(List.of(topic));
        return consumer;
    }

    private void sendOrderNotification() {
        Long id = 1L;
        byte[] correlationId = String.valueOf(id).getBytes(UTF_8);
        String topic = customKafkaProperties.getNotificationTopic().getName();
        String message = toJson(OrderMessage.builder().id(id).status(READY).build());
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        record.headers().add(CORRELATION_ID, correlationId);
        template.send(record);
        template.flush();
    }

    private Order getOrderIfReady() throws InterruptedException {
        Order result;
        for (int i = 0; i < 50; i++) {
            if ((result = orderRepository.findById(ORDER_ID).get()).getStatus() == READY) {
                return result;
            }
            Thread.sleep(100);
        }
        throw new RuntimeException("Timeout: order status update takes too much time");
    }

}

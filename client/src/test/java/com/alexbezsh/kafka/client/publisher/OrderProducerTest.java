package com.alexbezsh.kafka.client.publisher;

import com.alexbezsh.kafka.client.mapper.OrderMapper;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.producer.OrderProducer;
import com.alexbezsh.kafka.client.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.client.properties.CustomKafkaProperties.Topic;
import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static com.alexbezsh.kafka.client.TestUtils.order;
import static com.alexbezsh.kafka.common.CommonTestUtils.DATE_TIME;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.SOURCE_HEADER;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @InjectMocks
    private OrderProducer testedInstance;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CustomKafkaProperties properties;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, String>> producerRecordCaptor;

    @Test
    void sendNewOrder() {
        String messageSource = "client";
        Topic orderTopic = new Topic("orderTopic", 1, (short) 1);
        Topic notificationTopic = new Topic("notificationTopic", 1, (short) 1);
        Order order = order(OrderStatus.COOKING, DATE_TIME);
        OrderMessage orderMessage = orderMessage(OrderStatus.COOKING);

        doReturn(orderTopic).when(properties).getOrderTopic();
        doReturn(notificationTopic).when(properties).getNotificationTopic();
        doReturn(SOURCE_HEADER).when(properties).getMessageSourceHeader();
        doReturn(messageSource).when(properties).getMessageSource();
        doReturn(orderMessage).when(orderMapper).toOrderMessage(order);

        testedInstance.sendNewOrder(order);

        verify(kafkaTemplate).send(producerRecordCaptor.capture());

        ProducerRecord<String, String> actual = producerRecordCaptor.getValue();

        assertEquals(toJson(orderMessage), actual.value());
        Headers headers = actual.headers();
        assertEquals(String.valueOf(ORDER_ID),
            new String(headers.lastHeader(CORRELATION_ID).value()));
        assertEquals(notificationTopic.getName(),
            new String(headers.lastHeader(REPLY_TOPIC).value()));
        assertEquals(messageSource, new String(headers.lastHeader(SOURCE_HEADER).value()));
    }

}

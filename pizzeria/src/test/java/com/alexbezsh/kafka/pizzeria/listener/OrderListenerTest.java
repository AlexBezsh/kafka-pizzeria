package com.alexbezsh.kafka.pizzeria.listener;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.pizzeria.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.pizzeria.service.OrderService;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import static com.alexbezsh.kafka.common.CommonTestUtils.SOURCE_HEADER;
import static com.alexbezsh.kafka.common.CommonTestUtils.TEST_CORRELATION_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.TEST_REPLY_TOPIC;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.COOKING;
import static com.alexbezsh.kafka.common.model.OrderStatus.READY;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    private static final String TEST_MESSAGE = toJson(orderMessage(COOKING));

    @InjectMocks
    private OrderListener testedInstance;

    @Mock
    private OrderService orderService;

    @Mock
    private CustomKafkaProperties properties;

    @Test
    void partitionZeroListenerTest() {
        testListener(() -> testedInstance.partitionZeroListener(
            TEST_MESSAGE, TEST_REPLY_TOPIC, TEST_CORRELATION_ID));
    }

    @Test
    void partitionOneListenerTest() {
        testListener(() -> testedInstance.partitionOneListener(
            TEST_MESSAGE, TEST_REPLY_TOPIC, TEST_CORRELATION_ID));
    }

    @Test
    void partitionTwoListenerTest() {
        testListener(() -> testedInstance.partitionTwoListener(
            TEST_MESSAGE, TEST_REPLY_TOPIC, TEST_CORRELATION_ID));
    }

    private void testListener(Supplier<Message<?>> listener) {
        String source = "pizzeria";
        OrderMessage inputMessage = orderMessage(COOKING);
        OrderMessage outputMessage = orderMessage(READY);

        doAnswer(invocation -> {
            assertEquals(inputMessage, invocation.getArgument(0));
            return null;
        }).when(orderService).process(any());
        doReturn(SOURCE_HEADER).when(properties).getMessageSourceHeader();
        doReturn(source).when(properties).getMessageSource();

        Message<?> message = listener.get();

        assertEquals(toJson(outputMessage), message.getPayload());

        MessageHeaders headers = message.getHeaders();
        assertEquals(source, headers.get(SOURCE_HEADER));
        assertEquals(TEST_REPLY_TOPIC, headers.get(REPLY_TOPIC));
        assertEquals(TEST_CORRELATION_ID, headers.get(CORRELATION_ID));

        verify(orderService).process(any());
    }

}

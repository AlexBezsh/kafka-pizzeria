package com.alexbezsh.kafka.courier.listener;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import com.alexbezsh.kafka.courier.service.OrderService;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import static com.alexbezsh.kafka.common.CommonTestUtils.SOURCE_HEADER;
import static com.alexbezsh.kafka.common.CommonTestUtils.TEST_CORRELATION_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.DELIVERED;
import static com.alexbezsh.kafka.common.model.OrderStatus.READY;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    private static final String TEST_MESSAGE = toJson(orderMessage(READY));

    @InjectMocks
    private OrderListener testedInstance;

    @Mock
    private OrderService orderService;

    @Mock
    private CustomKafkaProperties properties;

    @Test
    void partitionZeroListenerTest() {
        testListener(
            () -> testedInstance.partitionZeroListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    @Test
    void partitionOneListenerTest() {
        testListener(
            () -> testedInstance.partitionOneListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    @Test
    void partitionTwoListenerTest() {
        testListener(
            () -> testedInstance.partitionTwoListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    private void testListener(Supplier<Message<?>> listener) {
        String source = "courier";
        OrderMessage inputMessage = orderMessage(READY);
        OrderMessage outputMessage = orderMessage(DELIVERED);

        doAnswer(invocation -> {
            assertEquals(inputMessage, invocation.getArgument(0));
            return null;
        }).when(orderService).process(any());
        doReturn(SOURCE_HEADER).when(properties).getMessageSourceHeader();
        doReturn(source).when(properties).getMessageSource();

        Message<?> message = listener.get();

        assertEquals(toJson(outputMessage), message.getPayload());
        assertEquals(source, message.getHeaders().get(SOURCE_HEADER));
        assertEquals(TEST_CORRELATION_ID, message.getHeaders().get(CORRELATION_ID));

        verify(orderService).process(any());
    }

}

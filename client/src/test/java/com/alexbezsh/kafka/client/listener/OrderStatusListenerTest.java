package com.alexbezsh.kafka.client.listener;

import com.alexbezsh.kafka.client.service.OrderService;
import com.alexbezsh.kafka.common.model.OrderMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.kafka.common.CommonTestUtils.TEST_CORRELATION_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.DELIVERED;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderStatusListenerTest {

    public static final OrderMessage ORDER_MESSAGE = orderMessage(DELIVERED);
    private static final String TEST_MESSAGE = toJson(ORDER_MESSAGE);

    @InjectMocks
    private OrderStatusListener testedInstance;

    @Mock
    private OrderService orderService;

    @Test
    void partitionZeroListenerTest() {
        testListener(() -> testedInstance.partitionZeroListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    @Test
    void partitionOneListenerTest() {
        testListener(() -> testedInstance.partitionOneListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    @Test
    void partitionTwoListenerTest() {
        testListener(() -> testedInstance.partitionTwoListener(TEST_MESSAGE, TEST_CORRELATION_ID));
    }

    private void testListener(Runnable listener) {
        listener.run();

        verify(orderService).updateOrderStatus(ORDER_MESSAGE);
    }

}

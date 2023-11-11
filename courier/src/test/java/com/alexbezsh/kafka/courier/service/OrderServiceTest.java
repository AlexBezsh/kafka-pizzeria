package com.alexbezsh.kafka.courier.service;

import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.READY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService testedInstance;

    @Mock
    private CustomKafkaProperties properties;

    @Test
    void processOrderTest() {
        doReturn(1).when(properties).getDeliveryTimeMs();

        assertDoesNotThrow(() -> testedInstance.process(orderMessage(READY)));
    }

}

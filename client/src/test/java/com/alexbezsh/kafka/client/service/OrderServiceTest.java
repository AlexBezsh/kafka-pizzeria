package com.alexbezsh.kafka.client.service;

import com.alexbezsh.kafka.client.exception.OrderNotFoundException;
import com.alexbezsh.kafka.client.mapper.OrderMapper;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.model.db.Pizza;
import com.alexbezsh.kafka.client.producer.OrderProducer;
import com.alexbezsh.kafka.client.repository.OrderItemRepository;
import com.alexbezsh.kafka.client.repository.OrderRepository;
import com.alexbezsh.kafka.client.repository.PizzaRepository;
import com.alexbezsh.kafka.common.model.OrderMessage;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.kafka.client.TestUtils.order;
import static com.alexbezsh.kafka.client.TestUtils.orderDto;
import static com.alexbezsh.kafka.client.TestUtils.orderRequest;
import static com.alexbezsh.kafka.client.TestUtils.pizzas;
import static com.alexbezsh.kafka.common.CommonTestUtils.DATE_TIME;
import static com.alexbezsh.kafka.common.CommonTestUtils.FIXED_CLOCK;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_2_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static com.alexbezsh.kafka.common.model.OrderStatus.COOKING;
import static com.alexbezsh.kafka.common.model.OrderStatus.READY;
import static java.time.ZoneId.systemDefault;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService testedInstance;

    @Spy
    private Clock clock = FIXED_CLOCK;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private OrderProducer orderProducer;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void placeOrder() {
        OrderRequest request = orderRequest();
        List<Pizza> pizzas = pizzas();
        Order order = order(COOKING, DATE_TIME);
        OrderDto expected = orderDto();

        doReturn(pizzas).when(pizzaRepository).findAllWhereIdIn(List.of(PIZZA_1_ID, PIZZA_2_ID));
        doReturn(order).when(orderMapper).toDbOrder(request, pizzas);
        doReturn(expected).when(orderMapper).toOrderDto(order);

        InOrder inOrder = inOrder(orderRepository, orderItemRepository,
            orderProducer, orderMapper);

        OrderDto actual = testedInstance.placeOrder(request);

        assertEquals(expected, actual);

        inOrder.verify(orderRepository).save(order);
        inOrder.verify(orderItemRepository).saveAll(order.getItems());
        inOrder.verify(orderProducer).sendNewOrder(order);
        inOrder.verify(orderMapper).toOrderDto(order);
    }

    @Test
    void getOrder() {
        Order order = order(COOKING, DATE_TIME);
        OrderDto expected = orderDto();

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(expected).when(orderMapper).toOrderDto(order);

        OrderDto actual = testedInstance.getOrder(ORDER_ID);

        assertEquals(expected, actual);
    }

    @Test
    void getOrderShouldThrowNotFoundException() {
        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        assertThrows(OrderNotFoundException.class, () -> testedInstance.getOrder(ORDER_ID));
    }

    @Test
    void updateOrderStatus() {
        OrderMessage orderMessage = orderMessage(READY);
        Order order = order(COOKING, DATE_TIME);
        LocalDateTime dateTime = DATE_TIME.plusMinutes(20);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(dateTime.atZone(systemDefault()).toInstant()).when(clock).instant();
        doReturn(systemDefault()).when(clock).getZone();
        doAnswer(invocation -> {
            Order orderArg = invocation.getArgument(0);
            assertEquals(orderMessage.getStatus(), orderArg.getStatus());
            assertEquals(DATE_TIME, orderArg.getCreated());
            assertEquals(dateTime, orderArg.getUpdated());
            return orderArg;
        }).when(orderRepository).save(order);

        testedInstance.updateOrderStatus(orderMessage);

        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatusShouldThrowNotFoundException() {
        OrderMessage orderMessage = orderMessage(READY);

        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        assertThrows(OrderNotFoundException.class,
            () -> testedInstance.updateOrderStatus(orderMessage));
    }

}

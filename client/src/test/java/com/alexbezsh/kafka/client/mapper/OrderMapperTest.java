package com.alexbezsh.kafka.client.mapper;

import com.alexbezsh.kafka.client.exception.PizzaNotFoundException;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static com.alexbezsh.kafka.client.TestUtils.order;
import static com.alexbezsh.kafka.client.TestUtils.orderDto;
import static com.alexbezsh.kafka.client.TestUtils.orderRequest;
import static com.alexbezsh.kafka.client.TestUtils.pizzas;
import static com.alexbezsh.kafka.common.CommonTestUtils.DATE_TIME;
import static com.alexbezsh.kafka.common.CommonTestUtils.FIXED_CLOCK;
import static com.alexbezsh.kafka.common.CommonTestUtils.orderMessage;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

    private final OrderMapper testedInstance = Mappers.getMapper(OrderMapper.class);

    @BeforeEach
    void setUp() {
        testedInstance.setClock(FIXED_CLOCK);
    }

    @Test
    void toDbOrder() {
        Order expected = order(OrderStatus.COOKING, DATE_TIME);
        expected.setId(null);
        expected.getItems().get(0).setId(null);
        expected.getItems().get(1).setId(null);

        Order actual = testedInstance.toDbOrder(orderRequest(), pizzas());

        assertEquals(expected, actual);
    }

    @Test
    void toDbOrderShouldThrowPizzaNotFoundException() {
        OrderRequest request = orderRequest();
        request.getItems().get(0).setId(Long.MAX_VALUE);
        Order expected = order(OrderStatus.COOKING, DATE_TIME);
        expected.setId(null);
        expected.getItems().get(0).setId(null);
        expected.getItems().get(1).setId(null);

        assertThrows(PizzaNotFoundException.class,
            () -> testedInstance.toDbOrder(request, pizzas()));
    }

    @Test
    void toOrderDto() {
        Order order = order(OrderStatus.COOKING, DATE_TIME);
        OrderDto expected = orderDto();

        OrderDto actual = testedInstance.toOrderDto(order);

        assertEquals(expected, actual);
    }

    @Test
    void toOrderMessage() {
        Order order = order(OrderStatus.COOKING, DATE_TIME);
        OrderMessage expected = orderMessage(OrderStatus.COOKING);

        OrderMessage actual = testedInstance.toOrderMessage(order);

        assertEquals(expected, actual);
    }

}

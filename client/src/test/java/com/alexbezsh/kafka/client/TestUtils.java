package com.alexbezsh.kafka.client;

import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.dto.OrderItemDto;
import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.api.request.OrderRequestItem;
import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.model.db.OrderItem;
import com.alexbezsh.kafka.client.model.db.Pizza;
import com.alexbezsh.kafka.common.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;
import static com.alexbezsh.kafka.common.CommonTestUtils.ADDRESS;
import static com.alexbezsh.kafka.common.CommonTestUtils.DATE_TIME;
import static com.alexbezsh.kafka.common.CommonTestUtils.ITEM_1_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.ITEM_1_QUANTITY;
import static com.alexbezsh.kafka.common.CommonTestUtils.ITEM_2_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.ITEM_2_QUANTITY;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_TOTAL;
import static com.alexbezsh.kafka.common.CommonTestUtils.PHONE_NUMBER;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_DESCRIPTION;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_NAME;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_PRICE;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_2_DESCRIPTION;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_2_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_2_NAME;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_2_PRICE;

@UtilityClass
public class TestUtils {

    public static final String UNEXPECTED_ERROR = "Unexpected error. Reason: null";

    public static OrderRequest orderRequest() {
        return OrderRequest.builder()
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .items(List.of(
                OrderRequestItem.builder()
                    .id(PIZZA_1_ID)
                    .quantity(ITEM_1_QUANTITY)
                    .build(),
                OrderRequestItem.builder()
                    .id(PIZZA_2_ID)
                    .quantity(ITEM_2_QUANTITY)
                    .build()))
            .build();
    }

    public static Order order(OrderStatus status, LocalDateTime updated) {
        return Order.builder()
            .id(ORDER_ID)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .status(status)
            .created(DATE_TIME)
            .updated(updated)
            .items(List.of(
                OrderItem.builder()
                    .id(ITEM_1_ID)
                    .quantity(ITEM_1_QUANTITY)
                    .name(PIZZA_1_NAME)
                    .price(PIZZA_1_PRICE)
                    .build(),
                OrderItem.builder()
                    .id(ITEM_2_ID)
                    .quantity(ITEM_2_QUANTITY)
                    .name(PIZZA_2_NAME)
                    .price(PIZZA_2_PRICE)
                    .build()))
            .build();
    }

    public static OrderDto orderDto() {
        String datetime = DATE_TIME.toString();
        return OrderDto.builder()
            .id(ORDER_ID)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .status(OrderStatus.COOKING)
            .created(datetime)
            .updated(datetime)
            .total(ORDER_TOTAL)
            .items(List.of(
                OrderItemDto.builder()
                    .id(ITEM_1_ID)
                    .quantity(ITEM_1_QUANTITY)
                    .name(PIZZA_1_NAME)
                    .price(PIZZA_1_PRICE)
                    .build(),
                OrderItemDto.builder()
                    .id(ITEM_2_ID)
                    .quantity(ITEM_2_QUANTITY)
                    .name(PIZZA_2_NAME)
                    .price(PIZZA_2_PRICE)
                    .build()))
            .build();
    }

    public static List<Pizza> pizzas() {
        return List.of(
            Pizza.builder()
                .id(PIZZA_1_ID)
                .name(PIZZA_1_NAME)
                .price(PIZZA_1_PRICE)
                .description(PIZZA_1_DESCRIPTION)
                .build(),
            Pizza.builder()
                .id(PIZZA_2_ID)
                .name(PIZZA_2_NAME)
                .price(PIZZA_2_PRICE)
                .description(PIZZA_2_DESCRIPTION)
                .build()
        );
    }

    public static List<PizzaDto> pizzaDtos() {
        return List.of(
            PizzaDto.builder()
                .id(PIZZA_1_ID)
                .name(PIZZA_1_NAME)
                .price(PIZZA_1_PRICE)
                .description(PIZZA_1_DESCRIPTION)
                .build(),
            PizzaDto.builder()
                .id(PIZZA_2_ID)
                .name(PIZZA_2_NAME)
                .price(PIZZA_2_PRICE)
                .description(PIZZA_2_DESCRIPTION)
                .build()
        );
    }

    public static MenuResponse menuResponse() {
        return MenuResponse.builder()
            .pizzas(pizzaDtos())
            .build();
    }

}

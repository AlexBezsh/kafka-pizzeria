package com.alexbezsh.kafka.common;

import com.alexbezsh.kafka.common.model.OrderMessage;
import com.alexbezsh.kafka.common.model.OrderMessageItem;
import com.alexbezsh.kafka.common.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.experimental.UtilityClass;
import static java.time.Instant.parse;

@UtilityClass
public class CommonTestUtils {

    public static final Clock FIXED_CLOCK =
        Clock.fixed(parse("2022-02-22T10:15:30.00Z"), ZoneId.systemDefault());
    public static final LocalDateTime DATE_TIME = LocalDateTime.now(FIXED_CLOCK);
    public static final Long ORDER_ID = 1L;
    public static final String ADDRESS = "Some address";
    public static final String PHONE_NUMBER = "1234567890";
    public static final Long PIZZA_1_ID = 1L;
    public static final Long PIZZA_2_ID = 2L;
    public static final String PIZZA_1_NAME = "Pizza 1";
    public static final String PIZZA_2_NAME = "Pizza 2";
    public static final String PIZZA_1_DESCRIPTION = "Pizza 1 description";
    public static final String PIZZA_2_DESCRIPTION = "Pizza 2 description";
    public static final BigDecimal PIZZA_1_PRICE = new BigDecimal("12.23");
    public static final BigDecimal PIZZA_2_PRICE = new BigDecimal("33.33");
    public static final Long ITEM_1_ID = 1L;
    public static final Long ITEM_2_ID = 2L;
    public static final Integer ITEM_1_QUANTITY = 1;
    public static final Integer ITEM_2_QUANTITY = 2;
    public static final BigDecimal ORDER_TOTAL = new BigDecimal("78.89");

    public static final String TEST_CORRELATION_ID = "1";
    public static final String SOURCE_HEADER = "messageSource";
    public static final String TEST_REPLY_TOPIC = "notification-topic";

    public static OrderMessage orderMessage(OrderStatus status) {
        return OrderMessage.builder()
            .id(ORDER_ID)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .status(status)
            .total(ORDER_TOTAL)
            .items(List.of(OrderMessageItem.builder()
                    .id(ITEM_1_ID)
                    .name(PIZZA_1_NAME)
                    .price(PIZZA_1_PRICE)
                    .quantity(ITEM_1_QUANTITY)
                    .build(),
                OrderMessageItem.builder()
                    .id(ITEM_2_ID)
                    .name(PIZZA_2_NAME)
                    .price(PIZZA_2_PRICE)
                    .quantity(ITEM_2_QUANTITY)
                    .build()))
            .created(DATE_TIME.toString())
            .build();
    }

}

package com.alexbezsh.kafka.common.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {

    private Long id;
    private String address;
    private String phoneNumber;
    private OrderStatus status;
    private String created;
    private BigDecimal total;
    private List<OrderMessageItem> items;

}

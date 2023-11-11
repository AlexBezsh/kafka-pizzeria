package com.alexbezsh.kafka.client.model.api.dto;

import com.alexbezsh.kafka.common.model.OrderStatus;
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
public class OrderDto {

    private Long id;
    private String address;
    private String phoneNumber;
    private OrderStatus status;
    private String created;
    private String updated;
    private BigDecimal total;
    private List<OrderItemDto> items;

}

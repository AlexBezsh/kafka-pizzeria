package com.alexbezsh.kafka.common.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageItem {

    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal price;

}

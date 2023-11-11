package com.alexbezsh.kafka.client.model.api.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;

}

package com.alexbezsh.kafka.client.model.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestItem {

    @NotNull
    private Long id;

    @Min(1)
    @NotNull
    private Integer quantity;

}

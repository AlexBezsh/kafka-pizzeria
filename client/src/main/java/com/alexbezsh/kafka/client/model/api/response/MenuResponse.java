package com.alexbezsh.kafka.client.model.api.response;

import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private List<PizzaDto> pizzas;

}

package com.alexbezsh.kafka.client.service;

import com.alexbezsh.kafka.client.mapper.PizzaMapper;
import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import com.alexbezsh.kafka.client.repository.PizzaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final PizzaMapper pizzaMapper;
    private final PizzaRepository pizzaRepository;

    public MenuResponse getMenu() {
        List<PizzaDto> pizzas = pizzaMapper.toDtos(pizzaRepository.findAll());
        return MenuResponse.builder()
            .pizzas(pizzas)
            .build();
    }

}

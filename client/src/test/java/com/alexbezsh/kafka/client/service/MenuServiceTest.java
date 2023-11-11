package com.alexbezsh.kafka.client.service;

import com.alexbezsh.kafka.client.mapper.PizzaMapper;
import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import com.alexbezsh.kafka.client.model.db.Pizza;
import com.alexbezsh.kafka.client.repository.PizzaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.kafka.client.TestUtils.menuResponse;
import static com.alexbezsh.kafka.client.TestUtils.pizzaDtos;
import static com.alexbezsh.kafka.client.TestUtils.pizzas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService testedInstance;

    @Mock
    private PizzaMapper pizzaMapper;

    @Mock
    private PizzaRepository pizzaRepository;

    @Test
    void getMenu() {
        List<Pizza> pizzas = pizzas();
        List<PizzaDto> pizzaDtos = pizzaDtos();
        MenuResponse expected = menuResponse();

        doReturn(pizzas).when(pizzaRepository).findAll();
        doReturn(pizzaDtos).when(pizzaMapper).toDtos(pizzas);

        MenuResponse actual = testedInstance.getMenu();

        assertEquals(expected, actual);
    }

}

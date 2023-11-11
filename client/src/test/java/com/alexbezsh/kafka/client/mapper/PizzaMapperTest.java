package com.alexbezsh.kafka.client.mapper;

import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import com.alexbezsh.kafka.client.model.db.Pizza;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static com.alexbezsh.kafka.client.TestUtils.pizzaDtos;
import static com.alexbezsh.kafka.client.TestUtils.pizzas;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PizzaMapperTest {

    private final PizzaMapper testedInstance = Mappers.getMapper(PizzaMapper.class);

    @Test
    void toDtos() {
        List<Pizza> entities = pizzas();
        List<PizzaDto> expected = pizzaDtos();

        List<PizzaDto> actual = testedInstance.toDtos(entities);

        assertEquals(expected, actual);
    }

}

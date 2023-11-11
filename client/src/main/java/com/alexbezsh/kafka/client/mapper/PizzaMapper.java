package com.alexbezsh.kafka.client.mapper;

import com.alexbezsh.kafka.client.model.api.dto.PizzaDto;
import com.alexbezsh.kafka.client.model.db.Pizza;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    List<PizzaDto> toDtos(List<Pizza> entities);

}

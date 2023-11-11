package com.alexbezsh.kafka.client.mapper;

import com.alexbezsh.kafka.client.exception.PizzaNotFoundException;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.api.request.OrderRequestItem;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.model.db.OrderItem;
import com.alexbezsh.kafka.client.model.db.Pizza;
import com.alexbezsh.kafka.common.model.OrderMessage;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    private Clock clock;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "COOKING")
    @Mapping(target = "items", expression = "java(toDbOrderItems(request, pizzas))")
    public abstract Order toDbOrder(OrderRequest request, List<Pizza> pizzas);

    @AfterMapping
    protected void setDatetime(@MappingTarget Order.OrderBuilder orderBuilder) {
        LocalDateTime datetime = LocalDateTime.now(clock)
            .truncatedTo(ChronoUnit.SECONDS);
        orderBuilder.created(datetime);
        orderBuilder.updated(datetime);
    }

    protected List<OrderItem> toDbOrderItems(OrderRequest request, List<Pizza> pizzas) {
        return request.getItems()
            .stream()
            .map(item -> toDbOrderItem(item, pizzas))
            .collect(Collectors.toList());
    }

    protected OrderItem toDbOrderItem(OrderRequestItem requestItem, List<Pizza> pizzas) {
        Pizza pizza = pizzas.stream()
            .filter(p -> p.getId().equals(requestItem.getId()))
            .findFirst()
            .orElseThrow(() -> new PizzaNotFoundException(requestItem.getId()));
        return toDbOrderItem(requestItem, pizza);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "pizza.name")
    @Mapping(target = "price", source = "pizza.price")
    @Mapping(target = "quantity", source = "requestItem.quantity")
    protected abstract OrderItem toDbOrderItem(OrderRequestItem requestItem, Pizza pizza);

    @Mapping(target = "total", expression = "java(countTotal(order))")
    @Mapping(target = "created", qualifiedByName = "datetimeToString")
    @Mapping(target = "updated", qualifiedByName = "datetimeToString")
    public abstract OrderDto toOrderDto(Order order);

    @Mapping(target = "total", expression = "java(countTotal(order))")
    @Mapping(target = "created", qualifiedByName = "datetimeToString")
    public abstract OrderMessage toOrderMessage(Order order);

    protected BigDecimal countTotal(Order order) {
        return order.getItems()
            .stream()
            .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("datetimeToString")
    protected String datetimeToString(LocalDateTime datetime) {
        return datetime.toString();
    }

}

package com.alexbezsh.kafka.client.service;

import com.alexbezsh.kafka.client.exception.OrderNotFoundException;
import com.alexbezsh.kafka.client.mapper.OrderMapper;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.api.request.OrderRequestItem;
import com.alexbezsh.kafka.client.model.db.Order;
import com.alexbezsh.kafka.client.model.db.Pizza;
import com.alexbezsh.kafka.client.producer.OrderProducer;
import com.alexbezsh.kafka.client.repository.OrderItemRepository;
import com.alexbezsh.kafka.client.repository.OrderRepository;
import com.alexbezsh.kafka.client.repository.PizzaRepository;
import com.alexbezsh.kafka.common.model.OrderMessage;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final Clock clock;
    private final OrderMapper orderMapper;
    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderDto placeOrder(OrderRequest request) {
        List<Pizza> pizzas = getPizzas(request);
        Order order = orderMapper.toDbOrder(request, pizzas);
        order.getItems().forEach(item -> item.setOrder(order));
        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());
        log.info("Order successfully saved. Id: {}", order.getId());
        orderProducer.sendNewOrder(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto getOrder(Long id) {
        return orderMapper.toOrderDto(findOrder(id));
    }

    @Transactional
    public void updateOrderStatus(OrderMessage orderMessage) {
        Order order = findOrder(orderMessage.getId());
        order.setStatus(orderMessage.getStatus());
        order.setUpdated(LocalDateTime.now(clock));
        orderRepository.save(order);
        log.info("Order status updated to {}", orderMessage.getStatus());
    }

    private List<Pizza> getPizzas(OrderRequest request) {
        List<Long> itemIds = request.getItems()
            .stream()
            .map(OrderRequestItem::getId)
            .collect(Collectors.toList());
        return pizzaRepository.findAllWhereIdIn(itemIds);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

}

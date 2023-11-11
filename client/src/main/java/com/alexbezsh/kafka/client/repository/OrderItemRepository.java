package com.alexbezsh.kafka.client.repository;

import com.alexbezsh.kafka.client.model.db.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

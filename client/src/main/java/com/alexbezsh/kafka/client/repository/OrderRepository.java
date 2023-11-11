package com.alexbezsh.kafka.client.repository;

import com.alexbezsh.kafka.client.model.db.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

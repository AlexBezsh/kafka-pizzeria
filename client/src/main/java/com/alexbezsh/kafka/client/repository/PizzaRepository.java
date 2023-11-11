package com.alexbezsh.kafka.client.repository;

import com.alexbezsh.kafka.client.model.db.Pizza;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    @Query("select p from Pizza p where id in :ids")
    List<Pizza> findAllWhereIdIn(List<Long> ids);

}

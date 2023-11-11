package com.alexbezsh.kafka.client.controller;

import com.alexbezsh.kafka.client.producer.OrderProducer;
import com.alexbezsh.kafka.client.service.MenuService;
import com.alexbezsh.kafka.client.service.OrderService;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest("spring.autoconfigure.exclude=" +
    "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration")
public class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected Clock clock;

    @MockBean
    protected MenuService menuService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected OrderProducer orderProducer;

}

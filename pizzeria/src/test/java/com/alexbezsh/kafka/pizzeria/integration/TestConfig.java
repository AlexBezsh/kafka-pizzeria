package com.alexbezsh.kafka.pizzeria.integration;

import com.alexbezsh.kafka.pizzeria.properties.CustomKafkaProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    public static final String NOTIFICATION_TOPIC_NAME = "notification-topic";

    @Bean
    public NewTopic orderTopic(CustomKafkaProperties properties) {
        return new NewTopic(properties.getOrderTopic().getName(), 3, (short) 1);
    }

    @Bean
    public NewTopic notificationTopic(CustomKafkaProperties properties) {
        return new NewTopic(NOTIFICATION_TOPIC_NAME, 3, (short) 1);
    }

}

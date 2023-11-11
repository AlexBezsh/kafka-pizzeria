package com.alexbezsh.kafka.courier.integration;

import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public NewTopic notificationTopic(CustomKafkaProperties properties) {
        return new NewTopic(properties.getNotificationTopic().getName(), 3, (short) 1);
    }

}

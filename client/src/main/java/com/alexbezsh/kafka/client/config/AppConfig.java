package com.alexbezsh.kafka.client.config;

import com.alexbezsh.kafka.client.properties.CustomKafkaProperties;
import java.time.Clock;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(CustomKafkaProperties.class)
public class AppConfig {

    @Bean
    @Profile("!test")
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public NewTopic orderTopic(CustomKafkaProperties properties) {
        return newTopic(properties.getOrderTopic());
    }

    @Bean
    public NewTopic notificationTopic(CustomKafkaProperties properties) {
        return newTopic(properties.getNotificationTopic());
    }

    private NewTopic newTopic(CustomKafkaProperties.Topic props) {
        return new NewTopic(props.getName(), props.getPartitions(), props.getReplicationFactor());
    }

}

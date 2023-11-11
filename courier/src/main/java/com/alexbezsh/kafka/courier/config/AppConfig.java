package com.alexbezsh.kafka.courier.config;

import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import java.util.Optional;
import org.apache.kafka.common.header.Header;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

@Configuration
public class AppConfig {

    @Bean
    public RecordFilterStrategy<String, String> messageFilter(CustomKafkaProperties properties) {
        return record -> {
            String source = Optional.ofNullable(record.headers())
                .map(headers -> headers.lastHeader(properties.getMessageSourceHeader()))
                .map(Header::value)
                .map(String::new)
                .orElseThrow(() -> new RuntimeException("Message source header is missing"));
            return properties.getMessageSource().equals(source);
        };
    }

}

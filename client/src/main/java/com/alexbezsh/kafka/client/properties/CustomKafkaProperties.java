package com.alexbezsh.kafka.client.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "kafka")
public class CustomKafkaProperties {

    @NotBlank
    private String messageSource;

    @NotBlank
    private String messageSourceHeader;

    @NotNull
    private Topic orderTopic;

    @NotNull
    private Topic notificationTopic;

    @Getter
    @AllArgsConstructor
    public static class Topic {

        @NotBlank
        private final String name;

        @NotNull
        @Positive
        private final Integer partitions;

        @NotNull
        @Positive
        private final Short replicationFactor;

    }

}

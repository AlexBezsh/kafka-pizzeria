package com.alexbezsh.kafka.courier.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Min(1)
    @NotNull
    private Integer deliveryTimeMs;

    @NotBlank
    private String messageSourceHeader;

    @NotNull
    private Topic notificationTopic;

    @Getter
    @AllArgsConstructor
    public static class Topic {

        @NotBlank
        private final String name;

    }

}

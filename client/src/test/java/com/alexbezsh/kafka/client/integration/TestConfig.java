package com.alexbezsh.kafka.client.integration;

import java.time.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static com.alexbezsh.kafka.common.CommonTestUtils.FIXED_CLOCK;

@TestConfiguration
public class TestConfig {

    @Bean
    public Clock clock() {
        return FIXED_CLOCK;
    }

}

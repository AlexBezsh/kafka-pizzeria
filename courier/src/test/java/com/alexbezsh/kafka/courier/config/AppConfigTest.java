package com.alexbezsh.kafka.courier.config;

import com.alexbezsh.kafka.courier.properties.CustomKafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import static com.alexbezsh.kafka.common.CommonTestUtils.SOURCE_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConfigTest {

    private final RecordFilterStrategy<String, String> messageFilter =
        new AppConfig().messageFilter(kafkaProperties());

    @ParameterizedTest
    @CsvSource({"pizzeria,false", "courier,true"})
    void messageFilterTest(String source, boolean expected) {
        Header[] headers = new Header[] {new RecordHeader(SOURCE_HEADER, source.getBytes(UTF_8))};
        ConsumerRecord<String, String> message = new ConsumerRecord<>("topic", 0, 0, 0L, null,
            0, 0, "", "", new RecordHeaders(headers), null);

        assertEquals(expected, messageFilter.filter(message));
    }

    private static CustomKafkaProperties kafkaProperties() {
        return CustomKafkaProperties.builder()
            .messageSourceHeader(SOURCE_HEADER)
            .messageSource("courier")
            .build();
    }

}

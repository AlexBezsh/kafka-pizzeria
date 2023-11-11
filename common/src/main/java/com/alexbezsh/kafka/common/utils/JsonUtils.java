package com.alexbezsh.kafka.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object obj) {
        if (obj == null) {
            throw new RuntimeException("Unable to convert null object to JSON");
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            String message = "Failed to convert POJO to JSON. Reason: " + e.getMessage();
            throw new RuntimeException(message, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null || type == null) {
            throw new RuntimeException("JSON string and POJO class must not be null");
        }
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            String message = String.format("Failed to convert JSON to %s. Reason: %s",
                type.getSimpleName(), e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

}

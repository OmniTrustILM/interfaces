package com.otilm.api.config.jackson3;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;

/** Reads and writes JSON the way a connector's REST API did on Spring Boot 3.5. */
final class Jackson2Mvc {

    private static final ObjectMapper MAPPER = JsonMapper
            .builder()
            .findAndAddModules()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .build();

    private Jackson2Mvc() {
    }

    static Object read(String json, Class<?> type, Class<?>... parameters) throws IOException {
        return MAPPER.readValue(json, javaType(type, parameters));
    }

    static String write(Object value, Class<?> type, Class<?>... parameters) throws IOException {
        return MAPPER.writerFor(javaType(type, parameters)).writeValueAsString(value);
    }

    static JsonNode tree(String json) throws IOException {
        return MAPPER.readTree(json);
    }

    private static JavaType javaType(Class<?> type, Class<?>... parameters) {
        return MAPPER.getTypeFactory().constructParametricType(type, parameters);
    }
}

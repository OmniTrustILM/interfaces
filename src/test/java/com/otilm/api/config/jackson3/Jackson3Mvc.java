package com.otilm.api.config.jackson3;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads and writes JSON the way a connector's REST API does on Spring Boot 4.1, which builds its mapper with
 * {@code findAndAddModules()} and these feature defaults.
 */
final class Jackson3Mvc {

    static final ObjectMapper MAPPER = JsonMapper
            .builder()
            .findAndAddModules(Jackson3Mvc.class.getClassLoader())
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

    private Jackson3Mvc() {
    }

    static Object read(String json, Class<?> type, Class<?>... parameters) {
        return MAPPER.readValue(json, javaType(type, parameters));
    }

    static String write(Object value, Class<?> type, Class<?>... parameters) {
        return MAPPER.writerFor(javaType(type, parameters)).writeValueAsString(value);
    }

    private static JavaType javaType(Class<?> type, Class<?>... parameters) {
        return MAPPER.getTypeFactory().constructParametricType(type, parameters);
    }
}

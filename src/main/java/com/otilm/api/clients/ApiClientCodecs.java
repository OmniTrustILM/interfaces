package com.otilm.api.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

/**
 * The JSON codecs every outbound {@code WebClient} this artifact builds is configured with.
 */
public final class ApiClientCodecs {

    /**
     * The two disabled features are wire contract: a connector adding a field must not break deserialization, and a
     * {@code @JsonView}-annotated DTO must not silently drop properties from a request.
     */
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper
            .builder()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .findAndAddModules()
            .build();

    private ApiClientCodecs() {
    }

    static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Binds the client to the Jackson 2 JSON codecs, which our DTO serializer annotations require. Left unset,
     * {@code defaultCodecs()} resolves JSON from the classpath and picks Jackson 3 from Spring Framework 7 onwards.
     *
     * @param codecs the configurer handed to {@code WebClient.Builder.codecs} or {@code ExchangeStrategies.builder}
     */
    public static void configureJsonCodecs(final ClientCodecConfigurer codecs) {
        configureJsonCodecs(codecs, OBJECT_MAPPER);
    }

    /**
     * Binds the client to the Jackson 2 JSON codecs, reading and writing through {@code mapper}. Use this where the
     * caller has its own wire mapper, which the no-arg form would replace.
     *
     * @param codecs the configurer handed to {@code WebClient.Builder.codecs} or {@code ExchangeStrategies.builder}
     * @param mapper the mapper both codecs are built on
     */
    @SuppressWarnings("removal")
    public static void configureJsonCodecs(final ClientCodecConfigurer codecs, final ObjectMapper mapper) {
        // Deprecated for removal on Framework 7.0; Jackson 2 support goes at 7.2, which is when this stops compiling.
        // The 7.x spelling is jacksonJsonDecoder/jacksonJsonEncoder, which do not exist on 6.2.
        codecs.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
        codecs.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
    }
}

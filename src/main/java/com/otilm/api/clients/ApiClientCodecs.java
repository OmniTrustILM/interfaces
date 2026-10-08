package com.otilm.api.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.util.ClassUtils;

/**
 * The JSON codecs every outbound {@code WebClient} this artifact builds is configured with.
 */
public final class ApiClientCodecs {

    /**
     * The modules Spring's {@code Jackson2ObjectMapperBuilder} registers when present, in its order. The wire depends
     * only on which of these a consumer has on its classpath.
     */
    private static final List<String> WELL_KNOWN_MODULES = List
            .of("com.fasterxml.jackson.datatype.jdk8.Jdk8Module",
                    "com.fasterxml.jackson.module.paramnames.ParameterNamesModule",
                    "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
                    "com.fasterxml.jackson.module.kotlin.KotlinModule");

    /**
     * Configured as Spring's {@code Jackson2ObjectMapperBuilder} configured it, which is the wire contract: unknown
     * properties do not fail, an active {@code @JsonView} writes only view-annotated properties, and a
     * {@code ProblemDetail} carries its extension members at the top level.
     */
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper
            .builder()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .addModules(wellKnownModulesOnTheClasspath())
            .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class)
            .build();

    private ApiClientCodecs() {
    }

    static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    private static List<Module> wellKnownModulesOnTheClasspath() {
        ClassLoader loader = ApiClientCodecs.class.getClassLoader();
        return WELL_KNOWN_MODULES
                .stream()
                .filter(name -> ClassUtils.isPresent(name, loader))
                .map(name -> (Module) BeanUtils.instantiateClass(ClassUtils.resolveClassName(name, loader)))
                .toList();
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

package com.otilm.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.Decoder;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

/**
 * Tests for Jackson 2 codec contract, which Spring Framework 7 would otherwise resolve to Jackson 3.
 */
class ApiClientCodecsTest {

    @Test
    void overridesAJsonDecoderThatWasAlreadyConfigured() {
        ClientCodecConfigurer codecs = ClientCodecConfigurer.create();
        ObjectMapper foreign = new ObjectMapper();
        codecs.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(foreign));

        ApiClientCodecs.configureJsonCodecs(codecs);

        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), jsonDecoder(codecs.getReaders()).getObjectMapper(),
                        "the seam must install its own Jackson 2 decoder");
    }

    @Test
    void overridesAJsonEncoderThatWasAlreadyConfigured() {
        ClientCodecConfigurer codecs = ClientCodecConfigurer.create();
        ObjectMapper foreign = new ObjectMapper();
        codecs.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(foreign));

        ApiClientCodecs.configureJsonCodecs(codecs);

        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), jsonEncoder(codecs.getWriters()).getObjectMapper(),
                        "the seam must install its own Jackson 2 encoder");
    }

    @Test
    void buildsTheCodecsOnACallerSuppliedMapper() {
        ClientCodecConfigurer codecs = ClientCodecConfigurer.create();
        ObjectMapper caller = new ObjectMapper();

        ApiClientCodecs.configureJsonCodecs(codecs, caller);

        Assertions.assertSame(caller, jsonDecoder(codecs.getReaders()).getObjectMapper(), "decoder mapper");
        Assertions.assertSame(caller, jsonEncoder(codecs.getWriters()).getObjectMapper(), "encoder mapper");
    }

    @Test
    void connectorClientsPinJacksonAndCarryTheTunedReadCap() {
        int readCap = 7 * 1024 * 1024;

        ExchangeStrategies strategies = BaseApiClient.connectorExchangeStrategies(tuningWithReadCap(readCap));
        Jackson2JsonDecoder decoder = jsonDecoder(strategies.messageReaders());

        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), decoder.getObjectMapper(),
                        "connector clients must decode through the pinned Jackson 2 codec");
        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), jsonEncoder(strategies.messageWriters()).getObjectMapper(),
                        "connector clients must encode through the pinned Jackson 2 codec");
        Assertions
                .assertEquals(readCap, decoder.getMaxInMemorySize(),
                        "the tuned read cap must reach the pinned decoder");
    }

    @Test
    void platformClientsPinJacksonAndCapAtTheDefaultReadCap() {
        ExchangeStrategies strategies = PlatformBaseApiClient.exchangeStrategies();
        Jackson2JsonDecoder decoder = jsonDecoder(strategies.messageReaders());

        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), decoder.getObjectMapper(),
                        "platform clients must decode through the pinned Jackson 2 codec");
        Assertions
                .assertSame(ApiClientCodecs.objectMapper(), jsonEncoder(strategies.messageWriters()).getObjectMapper(),
                        "platform clients must encode through the pinned Jackson 2 codec");
        Assertions
                .assertEquals(ClientTuning.DEFAULT_MAX_IN_MEMORY, decoder.getMaxInMemorySize(),
                        "platform clients cap at the default read cap");
    }

    @Test
    void connectorReadCapSurvivesPlatformStrategiesBuiltLater() {
        int connectorCap = 7 * 1024 * 1024;
        ExchangeStrategies connector = BaseApiClient.connectorExchangeStrategies(tuningWithReadCap(connectorCap));

        PlatformBaseApiClient.exchangeStrategies();

        Assertions
                .assertEquals(connectorCap, jsonDecoder(connector.messageReaders()).getMaxInMemorySize(),
                        "building the platform strategies must not rewrite the connector's read cap");
    }

    @Test
    void platformReadCapSurvivesConnectorStrategiesBuiltLater() {
        int differingConnectorCap = 7 * 1024 * 1024;
        ExchangeStrategies platform = PlatformBaseApiClient.exchangeStrategies();

        BaseApiClient.connectorExchangeStrategies(tuningWithReadCap(differingConnectorCap));

        Assertions
                .assertEquals(ClientTuning.DEFAULT_MAX_IN_MEMORY,
                        jsonDecoder(platform.messageReaders()).getMaxInMemorySize(),
                        "building the connector strategies must not rewrite the platform's read cap");
    }

    @Test
    void problemDetailWritesItsExtensionMembersAtTheTopLevel() {
        ProblemDetail problem = ProblemDetail.forStatus(422);
        problem.setProperty("causes", List.of(Map.of("field", "name")));

        JsonNode json = ApiClientCodecs.objectMapper().valueToTree(problem);

        Assertions.assertEquals("name", json.path("causes").path(0).path("field").asText(), "top-level causes");
        Assertions.assertFalse(json.has("properties"), "the properties map is flattened into the document");
    }

    private static ClientTuning tuningWithReadCap(final int readCap) {
        return new ClientTuning(Duration.ofSeconds(3), Duration.ofSeconds(35), 20, Duration.ofSeconds(10), readCap);
    }

    /** {@code StringDecoder} claims {@code application/json} too, so selection is by decoder type. */
    private static Jackson2JsonDecoder jsonDecoder(final List<HttpMessageReader<?>> readers) {
        return readers
                .stream()
                .filter(DecoderHttpMessageReader.class::isInstance)
                .map(reader -> ((DecoderHttpMessageReader<?>) reader).getDecoder())
                .filter(Jackson2JsonDecoder.class::isInstance)
                .map(Jackson2JsonDecoder.class::cast)
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError("no Jackson 2 JSON decoder is registered: " + decoderTypes(readers)));
    }

    private static Jackson2JsonEncoder jsonEncoder(final List<HttpMessageWriter<?>> writers) {
        return writers
                .stream()
                .filter(EncoderHttpMessageWriter.class::isInstance)
                .map(writer -> ((EncoderHttpMessageWriter<?>) writer).getEncoder())
                .filter(Jackson2JsonEncoder.class::isInstance)
                .map(Jackson2JsonEncoder.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("no Jackson 2 JSON encoder is registered"));
    }

    private static String decoderTypes(final List<HttpMessageReader<?>> readers) {
        return readers
                .stream()
                .filter(DecoderHttpMessageReader.class::isInstance)
                .map(reader -> ((DecoderHttpMessageReader<?>) reader).getDecoder())
                .map(Decoder::getClass)
                .map(Class::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");
    }
}

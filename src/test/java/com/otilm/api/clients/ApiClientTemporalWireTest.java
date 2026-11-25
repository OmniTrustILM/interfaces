package com.otilm.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormatResponseRequestDto;
import com.otilm.api.model.connector.signatures.formatting.TimestampingFormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.TimestampingFormatResponseRequestDto;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** The OpenAPI document declares connector dates and durations as ISO 8601 strings. */
class ApiClientTemporalWireTest {

    private static final String SIGNING_TIME = "2026-11-25T09:59:37.926306Z";

    private static final String ACCURACY = "PT0.001S";

    /** Reads JSON as a connector on Spring Boot 4.1 does. */
    private static final ObjectMapper CONNECTOR_MAPPER = JsonMapper
            .builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Test
    void computeDtbsSigningTimeIsAnIsoDateTime() {
        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        request.setSigningTime(OffsetDateTime.parse(SIGNING_TIME));

        JsonNode json = ApiClientCodecs.objectMapper().valueToTree(request);

        assertEquals(SIGNING_TIME, json.path("signingTime").textValue(), json.toString());
    }

    @Test
    void timestampingDtbsSigningTimeAndAccuracyAreIsoStrings() {
        JsonNode json = ApiClientCodecs.objectMapper().valueToTree(timestampingDtbsRequest());

        assertAll(() -> assertEquals(SIGNING_TIME, json.path("signingTime").textValue(), json.toString()),
                () -> assertEquals(ACCURACY, json.path("accuracy").textValue(), json.toString()));
    }

    @Test
    void timestampingResponseSigningTimeAndAccuracyAreIsoStrings() {
        JsonNode json = ApiClientCodecs.objectMapper().valueToTree(timestampingResponseRequest());

        assertAll(() -> assertEquals(SIGNING_TIME, json.path("signingTime").textValue(), json.toString()),
                () -> assertEquals(ACCURACY, json.path("accuracy").textValue(), json.toString()));
    }

    @Test
    void connectorReadsComputeDtbsSigningTimeBackExactly() throws JsonProcessingException {
        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        request.setSigningTime(OffsetDateTime.parse(SIGNING_TIME));

        ComputeDtbsRequestDto read = CONNECTOR_MAPPER
                .readValue(ApiClientCodecs.objectMapper().writeValueAsString(request), ComputeDtbsRequestDto.class);

        assertEquals(request.getSigningTime().toInstant(), read.getSigningTime().toInstant());
    }

    @Test
    void connectorReadsTimestampingDtbsTimesBackExactly() throws JsonProcessingException {
        TimestampingFormatDtbsRequestDto request = timestampingDtbsRequest();

        TimestampingFormatDtbsRequestDto read = (TimestampingFormatDtbsRequestDto) CONNECTOR_MAPPER
                .readValue(ApiClientCodecs.objectMapper().writeValueAsString(request), FormatDtbsRequestDto.class);

        assertAll(() -> assertEquals(request.getSigningTime(), read.getSigningTime()),
                () -> assertEquals(request.getAccuracy(), read.getAccuracy()));
    }

    @Test
    void connectorReadsTimestampingResponseTimesBackExactly() throws JsonProcessingException {
        TimestampingFormatResponseRequestDto request = timestampingResponseRequest();

        TimestampingFormatResponseRequestDto read = (TimestampingFormatResponseRequestDto) CONNECTOR_MAPPER
                .readValue(ApiClientCodecs.objectMapper().writeValueAsString(request), FormatResponseRequestDto.class);

        assertAll(() -> assertEquals(request.getSigningTime(), read.getSigningTime()),
                () -> assertEquals(request.getAccuracy(), read.getAccuracy()));
    }

    private static TimestampingFormatDtbsRequestDto timestampingDtbsRequest() {
        TimestampingFormatDtbsRequestDto request = new TimestampingFormatDtbsRequestDto();
        request.setSigningTime(Instant.parse(SIGNING_TIME));
        request.setAccuracy(Duration.parse(ACCURACY));
        return request;
    }

    private static TimestampingFormatResponseRequestDto timestampingResponseRequest() {
        TimestampingFormatResponseRequestDto request = new TimestampingFormatResponseRequestDto();
        request.setSigningTime(Instant.parse(SIGNING_TIME));
        request.setAccuracy(Duration.parse(ACCURACY));
        return request;
    }
}

package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryWindowDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static DiscoveryWindowDto window(DiscoveryWindowMode mode, Duration lookback) {
        DiscoveryWindowDto dto = new DiscoveryWindowDto();
        dto.setMode(mode);
        dto.setLookback(lookback);
        return dto;
    }

    @Test
    void serializesLookbackAsIsoDuration() throws Exception {
        String json = "{\"mode\":\"lookback\",\"lookback\":\"P1D\"}";

        DiscoveryWindowDto dto = mapper.readValue(json, DiscoveryWindowDto.class);

        assertEquals(DiscoveryWindowMode.LOOKBACK, dto.getMode());
        assertEquals(Duration.ofDays(1), dto.getLookback());

        JsonNode written = mapper.readTree(mapper.writeValueAsString(dto));
        assertTrue(written.get("lookback").isTextual(), "lookback must be written as an ISO-8601 string: " + written);
        assertEquals(Duration.ofDays(1), Duration.parse(written.get("lookback").asText()));
        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void omitsLookback_whenAbsent() throws Exception {
        String json = mapper.writeValueAsString(window(DiscoveryWindowMode.FULL, null));

        assertEquals("{\"mode\":\"full\"}", json);
    }

    @Test
    void rejectsLookback_whenModeIsNotLookback() {
        for (DiscoveryWindowMode mode : new DiscoveryWindowMode[]{
                DiscoveryWindowMode.FULL,
                DiscoveryWindowMode.SINCE_LAST_SUCCESSFUL_RUN}) {
            Set<ConstraintViolation<DiscoveryWindowDto>> violations = VALIDATOR
                    .validate(window(mode, Duration.ofDays(1)));

            assertFalse(violations.isEmpty(), "lookback must be refused for " + mode);
        }
    }

    @Test
    void reportsInconsistentLookback_onLookbackField() {
        Set<ConstraintViolation<DiscoveryWindowDto>> violations = VALIDATOR
                .validate(window(DiscoveryWindowMode.LOOKBACK, null));

        assertEquals(1, violations.size());
        ConstraintViolation<DiscoveryWindowDto> violation = violations.iterator().next();
        assertEquals("lookback", violation.getPropertyPath().toString());
        assertEquals("must be a positive duration, and given only with the lookback mode", violation.getMessage());
    }

    @Test
    void acceptsFullAndSinceLastSuccessfulRun_withoutLookback() {
        assertTrue(VALIDATOR.validate(window(DiscoveryWindowMode.FULL, null)).isEmpty());
        assertTrue(VALIDATOR.validate(window(DiscoveryWindowMode.SINCE_LAST_SUCCESSFUL_RUN, null)).isEmpty());
    }

    @Test
    void rejectsMissingLookback_whenModeIsLookback() {
        assertFalse(VALIDATOR.validate(window(DiscoveryWindowMode.LOOKBACK, null)).isEmpty());
    }

    @Test
    void rejectsNonPositiveLookback() {
        assertFalse(VALIDATOR.validate(window(DiscoveryWindowMode.LOOKBACK, Duration.parse("PT0S"))).isEmpty());
        assertFalse(VALIDATOR.validate(window(DiscoveryWindowMode.LOOKBACK, Duration.parse("-P1D"))).isEmpty());
    }

    @Test
    void rejectsMissingMode() {
        assertFalse(VALIDATOR.validate(window(null, null)).isEmpty());
    }

    @Test
    void rejectsUnknownMode() {
        Exception thrown = assertThrows(Exception.class,
                () -> mapper.readValue("{\"mode\":\"weekly\"}", DiscoveryWindowDto.class));

        assertTrue(thrown.getCause() instanceof ValidationException, String.valueOf(thrown));
    }
}

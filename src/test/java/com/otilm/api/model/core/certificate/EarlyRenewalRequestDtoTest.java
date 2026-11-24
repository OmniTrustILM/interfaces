package com.otilm.api.model.core.certificate;

import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EarlyRenewalRequestDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest
    @CsvSource({"3599, false", "3600, true", "2592000, true", "2592001, false"})
    void validatesTheSpreadBounds(long spreadSeconds, boolean valid) {
        assertEquals(valid, VALIDATOR.validate(request(spreadSeconds, null)).isEmpty());
    }

    @Test
    void requiresTheSpread() {
        assertFalse(VALIDATOR.validate(new EarlyRenewalRequestDto()).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"https://example.com/why, true", "http://example.com, true", "ftp://x, false", "example.com, false"})
    void validatesTheExplanationUrl(String url, boolean valid) {
        assertEquals(valid, VALIDATOR.validate(request(3600L, url)).isEmpty());
    }

    @Test
    void limitsTheExplanationUrlLength() {
        String prefix = "https://example.com/";
        assertTrue(VALIDATOR.validate(request(3600L, prefix + "a".repeat(2048 - prefix.length()))).isEmpty());
        assertFalse(VALIDATOR.validate(request(3600L, prefix + "a".repeat(2049 - prefix.length()))).isEmpty());
    }

    @Test
    void bulkRequestRequiresCertificateUuids() {
        var dto = new BulkEarlyRenewalRequestDto();
        dto.setSpreadSeconds(3600L);
        assertFalse(VALIDATOR.validate(dto).isEmpty());

        dto.setUuids(List.of());
        assertFalse(VALIDATOR.validate(dto).isEmpty());

        dto.setUuids(List.of(UUID.randomUUID()));
        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    private static EarlyRenewalRequestDto request(Long spreadSeconds, String explanationUrl) {
        var dto = new EarlyRenewalRequestDto();
        dto.setSpreadSeconds(spreadSeconds);
        dto.setExplanationUrl(explanationUrl);
        return dto;
    }
}

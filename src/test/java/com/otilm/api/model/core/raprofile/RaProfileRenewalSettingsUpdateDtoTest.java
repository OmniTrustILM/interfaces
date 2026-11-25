package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaProfileRenewalSettingsUpdateDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void hasTheDefaults_whenNothingIsSet() {
        var dto = new RaProfileRenewalSettingsUpdateDto();

        assertFalse(dto.isWindowEnabled());
        assertEquals(67, dto.getWindowStartPercent());
        assertEquals(80, dto.getWindowEndPercent());
        assertFalse(dto.isUseAuthorityRenewalInfo());
        assertFalse(dto.isAutoRenewalEnabled());
        assertEquals(5, dto.getAutoRenewalMaxAttempts());
        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"67, 80, true", "80, 80, false", "90, 80, false", "1, 99, true"})
    void isValidRequiresTheStartBeforeTheEnd(int start, int end, boolean expected) {
        var dto = settings(start, end);

        assertEquals(expected, dto.isValid());
        assertEquals(expected, VALIDATOR.validate(dto).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"0, 80", "67, 100", "-5, 80"})
    void isInvalid_whenAPercentageIsOutOfRange(int start, int end) {
        assertFalse(VALIDATOR.validate(settings(start, end)).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"0, false", "1, true", "20, true", "21, false"})
    void validatesTheMaxAttemptsRange(int attempts, boolean expected) {
        var dto = new RaProfileRenewalSettingsUpdateDto();
        dto.setAutoRenewalMaxAttempts(attempts);

        assertEquals(expected, VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void doesNotSerializeTheValidityCheck() throws Exception {
        var json = mapper.readTree(mapper.writeValueAsString(new RaProfileRenewalSettingsUpdateDto()));

        assertFalse(json.has("valid"));
        assertEquals(67, json.get("windowStartPercent").asInt());
    }

    private static RaProfileRenewalSettingsUpdateDto settings(int start, int end) {
        var dto = new RaProfileRenewalSettingsUpdateDto();
        dto.setWindowStartPercent(start);
        dto.setWindowEndPercent(end);
        return dto;
    }
}

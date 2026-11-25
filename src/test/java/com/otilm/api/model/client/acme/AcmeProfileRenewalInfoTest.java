package com.otilm.api.model.client.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.acme.AcmeProfileDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeProfileRenewalInfoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void defaultsAreDisabledWithSixHoursRetryAfter() {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        AcmeProfileDto dto = new AcmeProfileDto();

        assertEquals(Boolean.FALSE, request.getRenewalInfoEnabled());
        assertEquals(21600, request.getRenewalInfoRetryAfter());
        assertEquals(Boolean.FALSE, dto.getRenewalInfoEnabled());
        assertEquals(21600, dto.getRenewalInfoRetryAfter());
    }

    @Test
    void editLeavesRenewalInfoUnsetWhenOmitted() throws Exception {
        AcmeProfileEditRequestDto omitted = new ObjectMapper().readValue("{}", AcmeProfileEditRequestDto.class);
        AcmeProfileEditRequestDto explicitNull = new ObjectMapper()
                .readValue("{\"renewalInfoEnabled\":null,\"renewalInfoRetryAfter\":null}",
                        AcmeProfileEditRequestDto.class);

        assertNull(omitted.getRenewalInfoEnabled());
        assertNull(omitted.getRenewalInfoRetryAfter());
        assertNull(explicitNull.getRenewalInfoEnabled());
        assertNull(explicitNull.getRenewalInfoRetryAfter());
        assertTrue(VALIDATOR.validate(omitted).isEmpty());
    }

    @Test
    void requestRetryAfterBoundsAreEnforced() {
        assertBounds(value -> {
            AcmeProfileRequestDto dto = new AcmeProfileRequestDto();
            dto.setRenewalInfoRetryAfter(value);
            return VALIDATOR
                    .validate(dto)
                    .stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("renewalInfoRetryAfter"));
        });
    }

    @Test
    void editRetryAfterBoundsAreEnforced() {
        assertBounds(value -> {
            AcmeProfileEditRequestDto dto = new AcmeProfileEditRequestDto();
            dto.setRenewalInfoRetryAfter(value);
            return VALIDATOR
                    .validate(dto)
                    .stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("renewalInfoRetryAfter"));
        });
    }

    private static void assertBounds(Predicate<Integer> violates) {
        assertTrue(violates.test(299));
        assertFalse(violates.test(300));
        assertFalse(violates.test(86400));
        assertTrue(violates.test(86401));
    }
}

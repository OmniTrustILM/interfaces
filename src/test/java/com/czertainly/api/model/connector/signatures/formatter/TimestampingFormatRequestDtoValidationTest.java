package com.czertainly.api.model.connector.signatures.formatter;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the optionality of the timestamp {@code accuracy} field.
 *
 * <p>RFC 3161 makes the TSTInfo accuracy optional, and the timestamp-formatter-connector omits it
 * from the token when absent. A non-qualified, local-clock timestamp has no accuracy, so Core must be
 * able to send these requests without an accuracy value — i.e. the field must not be {@code @NotNull}.</p>
 */
class TimestampingFormatRequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void teardown() {
        factory.close();
    }

    @Test
    void dtbsRequest_nullAccuracy_producesNoAccuracyViolation() {
        TimestampingFormatDtbsRequestDto dto = new TimestampingFormatDtbsRequestDto();
        dto.setAccuracy(null);

        Set<ConstraintViolation<TimestampingFormatDtbsRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("accuracy")),
                "accuracy must be optional (RFC 3161) so non-qualified local-clock timestamps can be requested");
    }

    @Test
    void responseRequest_nullAccuracy_producesNoAccuracyViolation() {
        TimestampingFormatResponseRequestDto dto = new TimestampingFormatResponseRequestDto();
        dto.setAccuracy(null);

        Set<ConstraintViolation<TimestampingFormatResponseRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("accuracy")),
                "accuracy must be optional (RFC 3161) so non-qualified local-clock timestamps can be requested");
    }
}

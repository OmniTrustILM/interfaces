package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;

class KeyRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("blankValues")
    void keyRequest_rejectsBlankName(String blankName) {
        // given
        KeyRequestDto request = validRequest();
        request.setName(blankName);

        // when
        Set<ConstraintViolation<KeyRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertEquals(1, violations.size());
        assertHasViolation(violations, "name", "must not be blank");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("blankValues")
    void keyRequest_rejectsBlankDescription(String blankDescription) {
        // given
        KeyRequestDto request = validRequest();
        request.setDescription(blankDescription);

        // when
        Set<ConstraintViolation<KeyRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertEquals(1, violations.size());
        assertHasViolation(violations, "description", "must not be blank");
    }

    @Test
    void keyRequest_acceptsPopulatedTextAndEmptyAttributes() {
        // given
        KeyRequestDto request = validRequest();

        // when
        Set<ConstraintViolation<KeyRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertNoViolations(violations);
    }

    private static Stream<Named<String>> blankValues() {
        return Stream
                .of(named("null", null), named("empty", ""), named("spaces", "   "),
                        named("tabs and line breaks", "\t\r\n"));
    }

    private static KeyRequestDto validRequest() {
        KeyRequestDto request = new KeyRequestDto();
        request.setName("Signing key");
        request.setDescription("Production signing key (tenant A).");
        request.setAttributes(List.of());
        return request;
    }
}

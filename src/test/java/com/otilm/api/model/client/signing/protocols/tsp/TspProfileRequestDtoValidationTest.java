package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TspProfileRequestDtoValidationTest {

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

    private TspProfileRequestDto validBase() {
        TspProfileRequestDto dto = new TspProfileRequestDto();
        dto.setName("TSP-Profile-1");
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE));
        return dto;
    }

    @Test
    void basicPasswordAllowed_withVaultProfile_isValid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.BASIC_PASSWORD));
        dto.setVaultProfileUuid(UUID.fromString("6b55de1c-844f-11ec-a8a3-0242ac120002"));

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void basicPasswordAllowed_withoutVaultProfile_isInvalid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.BASIC_PASSWORD));
        // vaultProfileUuid intentionally left null

        Set<ConstraintViolation<TspProfileRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                        .equals("VaultProfileRequiredForBasicPassword")));
    }

    @Test
    void basicPasswordNotAllowed_withoutVaultProfile_isValid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE));

        assertTrue(validator.validate(dto).isEmpty());
    }
}

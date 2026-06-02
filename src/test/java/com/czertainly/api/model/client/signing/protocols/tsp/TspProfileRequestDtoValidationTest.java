package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.core.signing.TspAuthenticationMethod;
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

    private TspBasicCredentialRequestDto credential() {
        TspBasicCredentialRequestDto c = new TspBasicCredentialRequestDto();
        c.setUsername("svc-account");
        c.setMappedUserUuid(UUID.fromString("6b55de1c-844f-11ec-a8a3-0242ac120002"));
        return c;
    }

    @Test
    void basicPasswordAllowed_withCredentials_isValid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.BASIC_PASSWORD));
        dto.setBasicCredentials(List.of(credential()));

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void basicPasswordAllowed_withoutCredentials_isInvalid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.BASIC_PASSWORD));
        dto.setBasicCredentials(List.of());

        Set<ConstraintViolation<TspProfileRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                        .equals("BasicCredentialsRequiredIfBasicPassword")));
    }

    @Test
    void basicPasswordNotAllowed_withoutCredentials_isValid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE));
        dto.setBasicCredentials(List.of());

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void basicPasswordAllowed_credentialWithBlankUsername_isInvalid() {
        TspProfileRequestDto dto = validBase();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.BASIC_PASSWORD));
        TspBasicCredentialRequestDto bad = new TspBasicCredentialRequestDto();
        bad.setUsername("  ");
        bad.setMappedUserUuid(UUID.fromString("6b55de1c-844f-11ec-a8a3-0242ac120002"));
        dto.setBasicCredentials(List.of(bad));

        Set<ConstraintViolation<TspProfileRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("basicCredentials[0].username")));
    }
}

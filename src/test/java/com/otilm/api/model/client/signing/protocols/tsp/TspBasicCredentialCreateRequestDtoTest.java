package com.otilm.api.model.client.signing.protocols.tsp;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TspBasicCredentialCreateRequestDtoTest {

    private static final UUID MAPPED_USER_UUID = UUID.fromString("6b55de1c-844f-11ec-a8a3-0242ac120002");

    private static ValidatorFactory factory;
    private static Validator validator;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @Test
    void passesValidation_whenAllFieldsPresent() {
        // given
        var request = validCreateRequest();

        // when
        var violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void reportsPasswordViolation_whenPasswordMissing() {
        // given — a create request with no password
        var request = validCreateRequest();
        request.setPassword(null);

        // when
        var violations = validator.validate(request);

        // then
        assertTrue(hasViolationOn(violations, "password"));
    }

    @Test
    void reportsPasswordViolation_whenPasswordBlank() {
        // given — a create request whose password is only whitespace
        var request = validCreateRequest();
        request.setPassword("   ");

        // when
        var violations = validator.validate(request);

        // then
        assertTrue(hasViolationOn(violations, "password"));
    }

    @Test
    void reportsViolation_whenUsernameMissing() {
        // given — a create request with no username
        var request = validCreateRequest();
        request.setUsername(null);

        // when
        var violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    void reportsViolation_whenMappedUserUuidMissing() {
        // given — a create request with no mapped user
        var request = validCreateRequest();
        request.setMappedUserUuid(null);

        // when
        var violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    void omitsPassword_whenSerializedToJson() throws Exception {
        // given — a create request carrying a write-only password
        var secret = "s3cr3t";
        var request = validCreateRequest();
        request.setPassword(secret);

        // when
        var json = mapper.writeValueAsString(request);

        // then
        assertFalse(json.contains("password"));
        assertFalse(json.contains(secret));
    }

    @Test
    void deserializesPassword_fromRequestBody() throws Exception {
        // given
        var username = "svc-account";
        var secret = "s3cr3t";
        var json = "{\"username\":\"" + username + "\",\"password\":\"" + secret + "\","
                + "\"mappedUserUuid\":\"" + MAPPED_USER_UUID + "\"}";

        // when
        TspBasicCredentialCreateRequestDto parsed = mapper.readValue(json, TspBasicCredentialCreateRequestDto.class);

        // then
        assertEquals(username, parsed.getUsername());
        assertEquals(secret, parsed.getPassword());
        assertEquals(MAPPED_USER_UUID, parsed.getMappedUserUuid());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static TspBasicCredentialCreateRequestDto validCreateRequest() {
        TspBasicCredentialCreateRequestDto dto = new TspBasicCredentialCreateRequestDto();
        dto.setUsername("svc-account");
        dto.setPassword("s3cr3t");
        dto.setMappedUserUuid(MAPPED_USER_UUID);
        return dto;
    }

    private static boolean hasViolationOn(Set<ConstraintViolation<TspBasicCredentialCreateRequestDto>> violations, String property) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(property));
    }
}

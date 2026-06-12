package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class TspBasicCredentialDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;
    private final ObjectMapper mapper = new ObjectMapper();

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
    void requestItem_missingMappedUserUuid_isInvalid() {
        TspBasicCredentialRequestDto dto = new TspBasicCredentialRequestDto();
        dto.setUsername("svc-account");
        // mappedUserUuid deliberately omitted

        Set<ConstraintViolation<TspBasicCredentialRequestDto>> violations = validator.validate(dto);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("mappedUserUuid")));
    }

    @Test
    void requestItem_passwordIsWriteOnly_neverSerializedToJson() throws Exception {
        TspBasicCredentialRequestDto dto = new TspBasicCredentialRequestDto();
        dto.setUsername("svc-account");
        dto.setPassword("s3cr3t");
        dto.setMappedUserUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        String json = mapper.writeValueAsString(dto);
        Assertions.assertFalse(json.contains("password"),
                "Request DTO password must not be serialized to JSON (write-only): " + json);
    }

    @Test
    void responseItem_neverSerializesPassword() throws Exception {
        TspBasicCredentialDto dto = new TspBasicCredentialDto();
        dto.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        dto.setUsername("svc-account");
        NameAndUuidDto mapped = new NameAndUuidDto();
        mapped.setUuid("22222222-2222-2222-2222-222222222222");
        mapped.setName("real-user");
        dto.setMappedUser(mapped);

        String json = mapper.writeValueAsString(dto);
        Assertions.assertFalse(json.toLowerCase().contains("password"),
                "Response DTO must never carry a password field: " + json);
        Assertions.assertTrue(json.contains("svc-account"));
    }
}

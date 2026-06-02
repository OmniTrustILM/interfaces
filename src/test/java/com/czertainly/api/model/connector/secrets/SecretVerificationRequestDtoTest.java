package com.czertainly.api.model.connector.secrets;

import com.czertainly.api.model.connector.secrets.content.BasicAuthSecretContent;
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

class SecretVerificationRequestDtoTest {

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
    void deserializesPolymorphicCandidateContent() throws Exception {
        String json = """
                {"name":"my-secret","type":"basicAuth",
                 "candidate":{"type":"basicAuth","username":"u","password":"p"}}
                """;
        SecretVerificationRequestDto dto = mapper.readValue(json, SecretVerificationRequestDto.class);

        Assertions.assertEquals("my-secret", dto.getName());
        Assertions.assertEquals(SecretType.BASIC_AUTH, dto.getType());
        Assertions.assertInstanceOf(BasicAuthSecretContent.class, dto.getCandidate());
        Assertions.assertEquals("p", ((BasicAuthSecretContent) dto.getCandidate()).getPassword());
    }

    @Test
    void responseRoundTrips() throws Exception {
        SecretVerificationResponseDto resp = SecretVerificationResponseDto.builder().match(true).build();
        String json = mapper.writeValueAsString(resp);
        SecretVerificationResponseDto back = mapper.readValue(json, SecretVerificationResponseDto.class);
        Assertions.assertTrue(back.isMatch());
    }

    @Test
    void candidateWithBlankCredentials_failsValidation() {
        BasicAuthSecretContent blankCandidate = new BasicAuthSecretContent();
        blankCandidate.setUsername("");
        blankCandidate.setPassword("");

        SecretVerificationRequestDto dto = SecretVerificationRequestDto.builder()
                .name("my-secret")
                .type(SecretType.BASIC_AUTH)
                .candidate(blankCandidate)
                .build();

        Set<ConstraintViolation<SecretVerificationRequestDto>> violations = validator.validate(dto);
        Assertions.assertFalse(violations.isEmpty(),
                "@Valid on candidate must cascade into BasicAuthSecretContent constraints");
        Assertions.assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("candidate")),
                "violation path must reference candidate field");
    }
}

package com.otilm.api.model.client.est;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.est.EstAuthenticationPolicy;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstProfileRequestDtoValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final UUID SECRET_UUID = UUID.fromString("0d4c1a0e-6f3b-4a8e-9a51-6b1f1b7c2e01");
    private static final UUID CA_CERTIFICATE_UUID = UUID.fromString("5b0e7c2a-91d4-4f0b-8f2e-3c6a7d9e1f02");
    private static final UUID TOKEN_PROFILE_UUID = UUID.fromString("a7c3e5f1-2b4d-4e6f-8a1c-3d5e7f9b1c03");

    private static final String RESERVED_NAME = "Name must not be an EST operation name or a dot segment";
    private static final String SECRETS_RULE = "Basic credential secrets are required for the raBasic policy and not allowed for other policies";
    private static final String CA_RULE = "Trusted CA certificates are required for the vendorMtls and raMtls policies and not allowed for other policies";
    private static final String KEY_RECOVERY_RULE = "Key recovery requires server key generation and a token profile, and its settings are not allowed without key recovery";

    private final ObjectMapper mapper = new ObjectMapper();

    private static EstProfileRequestDto valid(EstAuthenticationPolicy policy) {
        EstProfileRequestDto dto = new EstProfileRequestDto();
        dto.setName("est-devices");
        dto.setAuthenticationPolicy(policy);
        if (policy == EstAuthenticationPolicy.RA_BASIC) {
            dto.setBasicCredentialSecretUuids(List.of(SECRET_UUID));
        }
        if (policy == EstAuthenticationPolicy.VENDOR_MTLS || policy == EstAuthenticationPolicy.RA_MTLS) {
            dto.setTrustedCaCertificateUuids(List.of(CA_CERTIFICATE_UUID));
        }
        return dto;
    }

    @ParameterizedTest
    @EnumSource(EstAuthenticationPolicy.class)
    void eachPolicyWithItsReferencesIsValid(EstAuthenticationPolicy policy) {
        assertNoViolations(VALIDATOR.validate(valid(policy)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cacerts",
            "CaCerts",
            "simpleenroll",
            "SIMPLEREENROLL",
            "serverkeygen",
            "csrattrs",
            "fullcmc",
            ".",
            ".."})
    void reservedLabelsAreRejected(String name) {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setName(name);
        assertHasViolation(VALIDATOR.validate(dto), "nameNotReserved", RESERVED_NAME);
    }

    @Test
    void nameWithAReservedUriCharacterIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setName("est/devices");
        assertTrue(VALIDATOR.validate(dto).stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void missingNameIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setName(null);
        assertTrue(VALIDATOR.validate(dto).stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void missingPolicyIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.CERTIFICATE_REGISTRATION);
        dto.setAuthenticationPolicy(null);
        assertTrue(VALIDATOR
                .validate(dto)
                .stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("authenticationPolicy")));
    }

    @Test
    void raBasicWithoutSecretsIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setBasicCredentialSecretUuids(List.of());
        assertHasViolation(VALIDATOR.validate(dto), "basicCredentialSecretsConsistent", SECRETS_RULE);
    }

    @Test
    void secretsWithAnotherPolicyAreInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.CERTIFICATE_REGISTRATION);
        dto.setBasicCredentialSecretUuids(List.of(SECRET_UUID));
        assertHasViolation(VALIDATOR.validate(dto), "basicCredentialSecretsConsistent", SECRETS_RULE);
    }

    @Test
    void raMtlsWithoutCaCertificatesIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setTrustedCaCertificateUuids(null);
        assertHasViolation(VALIDATOR.validate(dto), "trustedCaCertificatesConsistent", CA_RULE);
    }

    @Test
    void caCertificatesWithRaBasicAreInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setTrustedCaCertificateUuids(List.of(CA_CERTIFICATE_UUID));
        assertHasViolation(VALIDATOR.validate(dto), "trustedCaCertificatesConsistent", CA_RULE);
    }

    @Test
    void keyRecoveryWithoutServerKeyGenerationIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setKeyRecovery(true);
        dto.setKeyRecoveryTokenProfileUuid(TOKEN_PROFILE_UUID);
        assertHasViolation(VALIDATOR.validate(dto), "keyRecoveryConsistent", KEY_RECOVERY_RULE);
    }

    @Test
    void keyRecoveryWithoutTokenProfileIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setServerKeyGeneration(true);
        dto.setKeyRecovery(true);
        assertHasViolation(VALIDATOR.validate(dto), "keyRecoveryConsistent", KEY_RECOVERY_RULE);
    }

    @Test
    void tokenProfileWithoutKeyRecoveryIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setServerKeyGeneration(true);
        dto.setKeyRecoveryTokenProfileUuid(TOKEN_PROFILE_UUID);
        assertHasViolation(VALIDATOR.validate(dto), "keyRecoveryConsistent", KEY_RECOVERY_RULE);
    }

    @Test
    void keyRecoveryWithoutKeyAttributeListIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setServerKeyGeneration(true);
        dto.setKeyRecovery(true);
        dto.setKeyRecoveryTokenProfileUuid(TOKEN_PROFILE_UUID);
        dto.setKeyRecoveryKeyAttributes(null);
        assertHasViolation(VALIDATOR.validate(dto), "keyRecoveryConsistent", KEY_RECOVERY_RULE);
    }

    @Test
    void keyRecoveryWithServerKeyGenerationAndTokenProfileIsValid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setServerKeyGeneration(true);
        dto.setKeyRecovery(true);
        dto.setKeyRecoveryTokenProfileUuid(TOKEN_PROFILE_UUID);
        assertNoViolations(VALIDATOR.validate(dto));
    }

    @Test
    void negativeRenewalThresholdIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setRenewalThreshold(-1);
        assertTrue(VALIDATOR
                .validate(dto)
                .stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("renewalThreshold")));
    }

    @Test
    void nullElementInSecretListIsAViolation() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_BASIC);
        dto.setBasicCredentialSecretUuids(Arrays.asList(SECRET_UUID, null));
        assertTrue(VALIDATOR
                .validate(dto)
                .stream()
                .anyMatch(v -> v.getPropertyPath().toString().startsWith("basicCredentialSecretUuids")));
    }

    @Test
    void omittedListsAndFlagsDeserializeToDefaults() throws Exception {
        EstProfileEditRequestDto dto = mapper
                .readValue("{\"authenticationPolicy\":\"certificateRegistration\"}", EstProfileEditRequestDto.class);
        assertEquals(List.of(), dto.getBasicCredentialSecretUuids());
        assertEquals(List.of(), dto.getTrustedCaCertificateUuids());
        assertTrue(dto.isAllowRenewal());
        assertTrue(dto.isAllowRekey());
        assertFalse(dto.isSystemDefault());
        assertFalse(dto.isServerKeyGeneration());
        assertEquals(10, dto.getIssueWaitTimeout());
        assertEquals(30, dto.getRetryAfter());
        assertEquals(30, dto.getServerKeyGenerationTimeout());
        assertNoViolations(VALIDATOR.validate(dto));
    }

    @Test
    void timingAtItsMinimumsIsValid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setIssueWaitTimeout(0);
        dto.setRetryAfter(1);
        dto.setServerKeyGenerationTimeout(1);
        assertNoViolations(VALIDATOR.validate(dto));
    }

    @Test
    void timingBelowItsMinimumsIsInvalid() {
        EstProfileRequestDto dto = valid(EstAuthenticationPolicy.RA_MTLS);
        dto.setIssueWaitTimeout(-1);
        dto.setRetryAfter(0);
        dto.setServerKeyGenerationTimeout(0);
        List<String> paths = VALIDATOR.validate(dto).stream().map(v -> v.getPropertyPath().toString()).toList();
        assertTrue(paths.containsAll(List.of("issueWaitTimeout", "retryAfter", "serverKeyGenerationTimeout")),
                paths::toString);
    }

    @Test
    void editRequestAppliesTheSameRules() {
        EstProfileEditRequestDto dto = new EstProfileEditRequestDto();
        dto.setAuthenticationPolicy(EstAuthenticationPolicy.VENDOR_MTLS);
        assertHasViolation(VALIDATOR.validate(dto), "trustedCaCertificatesConsistent", CA_RULE);
    }

    @Test
    void crossFieldGettersStayOutOfJson() throws Exception {
        String json = mapper.writeValueAsString(valid(EstAuthenticationPolicy.RA_BASIC));
        assertFalse(json.contains("Consistent"));
        assertFalse(json.contains("nameNotReserved"));
        assertTrue(json.contains("\"authenticationPolicy\":\"raBasic\""));
    }
}

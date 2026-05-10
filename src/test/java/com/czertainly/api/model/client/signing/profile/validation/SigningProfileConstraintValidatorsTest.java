package com.czertainly.api.model.client.signing.profile.validation;

import com.czertainly.api.model.client.signing.profile.SigningProfileRequestDto;
import com.czertainly.api.model.client.signing.profile.scheme.DelegatedSigningRequestDto;
import com.czertainly.api.model.client.signing.profile.scheme.StaticKeyManagedSigningRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.RawSigningWorkflowRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningProfileConstraintValidatorsTest {

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

    private <T> boolean hasViolationOn(Set<ConstraintViolation<T>> violations, String path) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(path));
    }

    private StaticKeyManagedSigningRequestDto managedScheme() {
        StaticKeyManagedSigningRequestDto scheme = new StaticKeyManagedSigningRequestDto();
        scheme.setCertificateUuid(UUID.randomUUID());
        return scheme;
    }

    private DelegatedSigningRequestDto delegatedScheme() {
        DelegatedSigningRequestDto scheme = new DelegatedSigningRequestDto();
        scheme.setConnectorUuid(UUID.randomUUID());
        return scheme;
    }

    private SigningProfileRequestDto profileRequest(Object scheme, Object workflow) {
        SigningProfileRequestDto dto = new SigningProfileRequestDto();
        dto.setName("test-profile");
        dto.setSigningScheme((com.czertainly.api.model.client.signing.profile.scheme.SigningSchemeRequestDto) scheme);
        dto.setWorkflow((com.czertainly.api.model.client.signing.profile.workflow.WorkflowRequestDto) workflow);
        return dto;
    }

    // --- ManagedSigningFormatterConnectorValidator ---

    @Test
    void managedTimestampingNullFormatterUuid_producesViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        assertTrue(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void managedTimestampingWithFormatterUuid_noFormatterViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setSignatureFormatterConnectorUuid(UUID.randomUUID());
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void delegatedTimestampingNullFormatterUuid_noFormatterViolation() {
        assertFalse(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), new TimestampingWorkflowRequestDto())),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void managedContentSigningNullFormatterUuid_producesViolation() {
        assertTrue(hasViolationOn(validator.validate(profileRequest(managedScheme(), new ContentSigningWorkflowRequestDto())),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void managedContentSigningWithFormatterUuid_noFormatterViolation() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setSignatureFormatterConnectorUuid(UUID.randomUUID());
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void managedRawSigningNullFormatterUuid_noFormatterViolation() {
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), new RawSigningWorkflowRequestDto())),
                "workflow.signatureFormatterConnectorUuid"));
    }

    @Test
    void managedTimestampingQualifiedTimestampTrueNullTqcUuid_producesViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setQualifiedTimestamp(true);
        assertTrue(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.timeQualityConfigurationUuid"));
    }

    @Test
    void managedTimestampingQualifiedTimestampTrueWithTqcUuid_noQualificationViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setSignatureFormatterConnectorUuid(UUID.randomUUID());
        workflow.setQualifiedTimestamp(true);
        workflow.setTimeQualityConfigurationUuid(UUID.randomUUID());
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.timeQualityConfigurationUuid"));
    }

    @Test
    void managedTimestampingQualifiedTimestampFalseNullTqcUuid_noQualificationViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setQualifiedTimestamp(false);
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.timeQualityConfigurationUuid"));
    }

    @Test
    void managedTimestampingQualifiedTimestampNullNullTqcUuid_noQualificationViolation() {
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), new TimestampingWorkflowRequestDto())),
                "workflow.timeQualityConfigurationUuid"));
    }

    @Test
    void delegatedTimestampingQualifiedTimestampTrueNullTqcUuid_noQualificationViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setQualifiedTimestamp(true);
        assertFalse(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.timeQualityConfigurationUuid"));
    }

    // --- OidValidator (via TimestampingWorkflowRequestDto.defaultPolicyId) ---

    @Test
    void validDefaultPolicyId_noOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.2.840.113549.1.9.14");
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void invalidDefaultPolicyId_producesOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("not-an-oid");
        assertTrue(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void nullDefaultPolicyId_noOidViolation() {
        assertFalse(hasViolationOn(validator.validate(new TimestampingWorkflowRequestDto()), "defaultPolicyId"));
    }

    // ASN.1 arc-constraint tests: first arc 0 or 1 → second arc must be 0..39

    @Test
    void oidFirstArc1SecondArc39_noOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.39.1");
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void oidFirstArc0SecondArc0_noOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("0.0.1");
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void oidFirstArc2SecondArcLarge_noOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("2.999.1");
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void oidFirstArc1SecondArc40_producesOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.40.1");
        assertTrue(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void oidFirstArc1SecondArc80_producesOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.80.1");
        assertTrue(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void oidFirstArc0SecondArc40_producesOidViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("0.40.1");
        assertTrue(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    // --- OidListValidator (via TimestampingWorkflowRequestDto.allowedPolicyIds) ---

    @Test
    void validOidList_noViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5", "2.16.840.1.101.3.4.2.1"));
        assertFalse(hasViolationOn(validator.validate(dto), "allowedPolicyIds"));
    }

    @Test
    void oidListWithInvalidElement_producesViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5", "not-an-oid"));
        assertTrue(hasViolationOn(validator.validate(dto), "allowedPolicyIds"));
    }

    @Test
    void nullOidList_noViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setAllowedPolicyIds(null);
        assertFalse(hasViolationOn(validator.validate(dto), "allowedPolicyIds"));
    }
}

package com.otilm.api.model.client.signing.profile.validation;

import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.client.signing.profile.SigningProfileRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.DelegatedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.StaticKeyManagedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.RawSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.InternalTimestampSourceRequestDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    private <T> boolean hasViolationWithMessage(Set<ConstraintViolation<T>> violations, String message) {
        return violations.stream().anyMatch(v -> v.getMessage().equals(message));
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

    private ContentSigningWorkflowRequestDto contentSigningWorkflow(SignatureLevel maxLevel) {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());
        workflow.setFamily(SignatureFamily.PADES);
        workflow.setMaxLevel(maxLevel);
        return workflow;
    }

    private SigningProfileRequestDto profileRequest(Object scheme, Object workflow) {
        SigningProfileRequestDto dto = new SigningProfileRequestDto();
        dto.setName("test-profile");
        dto.setSigningScheme((com.otilm.api.model.client.signing.profile.scheme.SigningSchemeRequestDto) scheme);
        dto.setWorkflow((com.otilm.api.model.client.signing.profile.workflow.WorkflowRequestDto) workflow);
        return dto;
    }

    // --- ManagedSignatureFormattingConnectorValidator ---

    @Test
    void managedTimestampingNullFormattingUuid_producesViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        assertTrue(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void managedTimestampingWithFormattingUuid_noFormattingViolation() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void delegatedTimestampingNullFormattingUuid_noFormattingViolation() {
        assertFalse(hasViolationOn(
                validator.validate(profileRequest(delegatedScheme(), new TimestampingWorkflowRequestDto())),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void managedContentSigningNullFormattingUuid_producesViolation() {
        assertTrue(hasViolationOn(
                validator.validate(profileRequest(managedScheme(), new ContentSigningWorkflowRequestDto())),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void managedContentSigningWithFormattingUuid_noFormattingViolation() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());
        assertFalse(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void managedRawSigningNullFormattingUuid_noFormattingViolation() {
        assertFalse(
                hasViolationOn(validator.validate(profileRequest(managedScheme(), new RawSigningWorkflowRequestDto())),
                        "workflow.signatureFormattingConnectorUuid"));
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
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());
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
        assertFalse(hasViolationOn(
                validator.validate(profileRequest(managedScheme(), new TimestampingWorkflowRequestDto())),
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

    // --- DefaultPolicyIdValidator ---

    @Test
    void defaultPolicyNotInAllowedPolicies_producesViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.2.3.4.7");
        dto.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5", "1.2.3.4.6"));
        assertTrue(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void defaultPolicyInAllowedPolicies_noViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.2.3.4.5");
        dto.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5", "1.2.3.4.6"));
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void defaultPolicyWithEmptyAllowedPolicies_noViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setDefaultPolicyId("1.2.3.4.5");
        dto.setAllowedPolicyIds(java.util.List.of());
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void nullDefaultPolicyWithAllowedPolicies_noViolation() {
        TimestampingWorkflowRequestDto dto = new TimestampingWorkflowRequestDto();
        dto.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5"));
        assertFalse(hasViolationOn(validator.validate(dto), "defaultPolicyId"));
    }

    @Test
    void defaultPolicyNotInAllowedPolicies_violationSurfacesThroughProfileRequest() {
        TimestampingWorkflowRequestDto workflow = new TimestampingWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());
        workflow.setDefaultPolicyId("1.2.3.4.7");
        workflow.setAllowedPolicyIds(java.util.List.of("1.2.3.4.5"));
        assertTrue(hasViolationOn(validator.validate(profileRequest(managedScheme(), workflow)),
                "workflow.defaultPolicyId"));
    }

    // --- ManagedSignatureFormattingConnectorValidator: content signing ---

    @Test
    void aTimestampedLevelWithoutASourceIsRejected() {
        for (SignatureLevel level : List
                .of(SignatureLevel.TIMESTAMPED, SignatureLevel.LONG_TERM, SignatureLevel.ARCHIVAL)) {
            Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                    .validate(profileRequest(managedScheme(), contentSigningWorkflow(level)));

            assertTrue(hasViolationOn(violations, "workflow.timestampSource"), level.name() + " was accepted");
        }
    }

    @Test
    void aTimestampedLevelWithASourceIsAccepted() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.ARCHIVAL);
        workflow.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertFalse(hasViolationOn(violations, "workflow.timestampSource"));
    }

    @Test
    void aTimestampSourceMissingItsProfileIsRejected() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.ARCHIVAL);
        workflow.setTimestampSource(new InternalTimestampSourceRequestDto(null));

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.timestampSource.signingProfileUuid"));
    }

    @Test
    void aManagedProfileWithoutAFamilyIsRejected() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.SIGNED);
        workflow.setFamily(null);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.family"));
    }

    @Test
    void aManagedProfileWithoutAMaxLevelIsRejected() {
        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), contentSigningWorkflow(null)));

        assertTrue(hasViolationOn(violations, "workflow.maxLevel"));
    }

    @Test
    void aManagedProfileMissingBothFamilyAndMaxLevelReportsBoth() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(null);
        workflow.setFamily(null);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.family"));
        assertTrue(hasViolationOn(violations, "workflow.maxLevel"));
    }

    @Test
    void aManagedProfileMissingFamilyStillReportsTheMissingTimestampSource() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.ARCHIVAL);
        workflow.setFamily(null);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.family"));
        assertTrue(hasViolationOn(violations, "workflow.timestampSource"));
    }

    @Test
    void aDelegatedProfileNeedsNoSignatureParameters() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(delegatedScheme(), workflow));

        assertFalse(hasViolationOn(violations, "workflow.family"));
        assertFalse(hasViolationOn(violations, "workflow.maxLevel"));
    }

    @Test
    void aDelegatedProfileCarryingAManagedLevelIsRejectedAtThatField() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setMaxLevel(SignatureLevel.TIMESTAMPED);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(delegatedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.maxLevel"));
        assertFalse(hasViolationOn(violations, "workflow.timestampSource"));
    }

    @Test
    void aDelegatedProfileCarryingASignatureFamilyIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setFamily(SignatureFamily.PADES);

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)), "workflow.family"));
    }

    @Test
    void aDelegatedProfileCarryingAFormattingConnectorIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorUuid(UUID.randomUUID());

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.signatureFormattingConnectorUuid"));
    }

    @Test
    void aDelegatedProfileCarryingFormattingAttributesIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setSignatureFormattingConnectorAttributes(List.of(new RequestAttributeV3()));

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.signatureFormattingConnectorAttributes"));
    }

    @Test
    void aDelegatedProfileDemandingNonRepudiationIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setRequireNonRepudiation(true);

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.requireNonRepudiation"));
    }

    @Test
    void aDelegatedProfileSendingAnExplicitFalseNonRepudiationIsAccepted() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setRequireNonRepudiation(false);

        assertFalse(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.requireNonRepudiation"));
    }

    @Test
    void aDelegatedProfileCarryingRequiredExtendedKeyUsagesIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.requiredExtendedKeyUsageOids"));
    }

    @Test
    void aDelegatedRawProfileDemandingNonRepudiationIsRejected() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setRequireNonRepudiation(true);

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.requireNonRepudiation"));
    }

    @Test
    void aDelegatedRawProfileCarryingRequiredExtendedKeyUsagesIsRejected() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.requiredExtendedKeyUsageOids"));
    }

    @Test
    void aDelegatedRawProfileWithNoManagedKnobsIsAccepted() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setRequireNonRepudiation(false);
        workflow.setRequiredExtendedKeyUsageOids(Set.of());

        assertTrue(validator.validate(profileRequest(delegatedScheme(), workflow)).isEmpty());
    }

    @Test
    void aDelegatedProfileWithTheDefaultCertificatePurposeConstraintsIsAccepted() {
        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(delegatedScheme(), new ContentSigningWorkflowRequestDto()));

        assertFalse(hasViolationOn(violations, "workflow.requireNonRepudiation"));
        assertFalse(hasViolationOn(violations, "workflow.requiredExtendedKeyUsageOids"));
    }

    @Test
    void aDelegatedProfileWithTheDefaultAttributeListIsAccepted() {
        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(delegatedScheme(), new ContentSigningWorkflowRequestDto()));

        assertFalse(hasViolationOn(violations, "workflow.signatureFormattingConnectorAttributes"));
    }

    @Test
    void aDelegatedProfileCarryingATimestampSourceIsRejected() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));

        assertTrue(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.timestampSource"));
    }

    @Test
    void aDelegatedProfileCarryingADocumentSizeCapIsAccepted() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setDocumentSizeCap(1L);

        assertFalse(hasViolationOn(validator.validate(profileRequest(delegatedScheme(), workflow)),
                "workflow.documentSizeCap"));
    }

    @Test
    void aManagedProfileAtSignedNeedsNoTimestampSource() {
        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), contentSigningWorkflow(SignatureLevel.SIGNED)));

        assertFalse(hasViolationOn(violations, "workflow.timestampSource"));
    }

    @Test
    void aManagedProfileAtSignedCarryingATimestampSourceIsRejected() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.SIGNED);
        workflow.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.timestampSource"));
        assertTrue(hasViolationWithMessage(violations, "timestampSource must be omitted when maxLevel is SIGNED"));
    }

    @Test
    void aDelegatedProfileAtNoLevelCarryingATimestampSourceIsRejectedAsDelegated() {
        ContentSigningWorkflowRequestDto workflow = new ContentSigningWorkflowRequestDto();
        workflow.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(delegatedScheme(), workflow));

        assertTrue(hasViolationWithMessage(violations, "timestampSource must be omitted for delegated signing"));
    }

    @Test
    void aNonPositiveDocumentSizeCapIsRejected() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.SIGNED);
        workflow.setDocumentSizeCap(0L);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertTrue(hasViolationOn(violations, "workflow.documentSizeCap"));
    }

    @Test
    void aPositiveDocumentSizeCapIsAccepted() {
        ContentSigningWorkflowRequestDto workflow = contentSigningWorkflow(SignatureLevel.SIGNED);
        workflow.setDocumentSizeCap(1L);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator
                .validate(profileRequest(managedScheme(), workflow));

        assertFalse(hasViolationOn(violations, "workflow.documentSizeCap"));
    }
}

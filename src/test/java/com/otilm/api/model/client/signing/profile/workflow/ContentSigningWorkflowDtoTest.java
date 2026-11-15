package com.otilm.api.model.client.signing.profile.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.InternalTimestampSourceDto;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.InternalTimestampSourceRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceType;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentSigningWorkflowDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    private ContentSigningWorkflowRequestDto fullyConfiguredRequest() {
        ContentSigningWorkflowRequestDto dto = new ContentSigningWorkflowRequestDto();
        dto.setSignatureFormattingConnectorUuid(UUID.randomUUID());
        dto.setFamily(new SignatureFamily("pades"));
        dto.setMaxLevel(SignatureLevel.ARCHIVAL);
        dto.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));
        dto.setRequireNonRepudiation(true);
        dto.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));
        return dto;
    }

    @Test
    void theRequestRoundTripsTheAllowList() throws Exception {
        ContentSigningWorkflowRequestDto original = fullyConfiguredRequest();
        original.setAllowedRequestAttributes(new LinkedHashSet<>(List.of("signatureContext", "reason")));

        String json = mapper.writeValueAsString(original);
        WorkflowRequestDto decoded = mapper.readValue(json, WorkflowRequestDto.class);

        ContentSigningWorkflowRequestDto workflow = assertInstanceOf(ContentSigningWorkflowRequestDto.class, decoded);
        assertEquals(Set.of("signatureContext", "reason"), workflow.getAllowedRequestAttributes());
    }

    @Test
    void theResponseRoundTripsTheAllowList() throws Exception {
        ContentSigningWorkflowDto original = new ContentSigningWorkflowDto();
        original.setAllowedRequestAttributes(new LinkedHashSet<>(List.of("signatureContext")));

        String json = mapper.writeValueAsString(original);
        WorkflowDto decoded = mapper.readValue(json, WorkflowDto.class);

        ContentSigningWorkflowDto workflow = assertInstanceOf(ContentSigningWorkflowDto.class, decoded);
        assertEquals(Set.of("signatureContext"), workflow.getAllowedRequestAttributes());
    }

    @Test
    void aBlankNameInTheAllowListIsRejected() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request.setAllowedRequestAttributes(new LinkedHashSet<>(List.of("signatureContext", " ")));

        Set<ConstraintViolation<ContentSigningWorkflowRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size(), violations.toString());
        ConstraintViolation<ContentSigningWorkflowRequestDto> violation = violations.iterator().next();
        assertEquals("allowedRequestAttributes[].<iterable element>", violation.getPropertyPath().toString());
        assertEquals("allowedRequestAttributes must not contain blank names", violation.getMessage());
    }

    @Test
    void theAllowListStartsEmptyRatherThanNullOnBothSurfaces() {
        assertEquals(Set.of(), new ContentSigningWorkflowRequestDto().getAllowedRequestAttributes());
        assertEquals(Set.of(), new ContentSigningWorkflowDto().getAllowedRequestAttributes());
    }

    @Test
    void theRequestRoundTripsThroughTheWorkflowUnion() throws Exception {
        ContentSigningWorkflowRequestDto original = fullyConfiguredRequest();

        String json = mapper.writeValueAsString(original);
        WorkflowRequestDto decoded = mapper.readValue(json, WorkflowRequestDto.class);

        ContentSigningWorkflowRequestDto workflow = assertInstanceOf(ContentSigningWorkflowRequestDto.class, decoded);
        assertEquals(SigningWorkflowType.CONTENT_SIGNING, workflow.getType());
        assertEquals(new SignatureFamily("pades"), workflow.getFamily());
        assertEquals(SignatureLevel.ARCHIVAL, workflow.getMaxLevel());
        assertEquals(TimestampSourceType.INTERNAL, workflow.getTimestampSource().getType());
        assertEquals(original.getTimestampSource(), workflow.getTimestampSource());
        assertEquals(Boolean.TRUE, workflow.getRequireNonRepudiation());
        assertEquals(Set.of("1.3.6.1.5.5.7.3.36"), workflow.getRequiredExtendedKeyUsageOids());
    }

    @Test
    void theResponseRoundTripsThroughTheWorkflowUnion() throws Exception {
        ContentSigningWorkflowDto original = new ContentSigningWorkflowDto();
        original.setFamily(new SignatureFamily("xades"));
        original.setMaxLevel(SignatureLevel.TIMESTAMPED);
        original.setTimestampSource(new InternalTimestampSourceDto(new NameAndUuidDto(UUID.randomUUID(), "tsa")));

        WorkflowDto decoded = mapper.readValue(mapper.writeValueAsString(original), WorkflowDto.class);

        ContentSigningWorkflowDto workflow = assertInstanceOf(ContentSigningWorkflowDto.class, decoded);
        assertEquals(new SignatureFamily("xades"), workflow.getFamily());
        assertEquals(SignatureLevel.TIMESTAMPED, workflow.getMaxLevel());
        assertEquals(TimestampSourceType.INTERNAL, workflow.getTimestampSource().getType());
        assertEquals(original.getTimestampSource(), workflow.getTimestampSource());
    }

    @Test
    void everyIlmManagedFieldIsOptionalOnTheWire() throws Exception {
        WorkflowRequestDto decoded = mapper.readValue("{\"type\":\"content_signing\"}", WorkflowRequestDto.class);

        ContentSigningWorkflowRequestDto workflow = assertInstanceOf(ContentSigningWorkflowRequestDto.class, decoded);
        assertNull(workflow.getFamily());
        assertNull(workflow.getMaxLevel());
        assertNull(workflow.getTimestampSource());
        assertNull(workflow.getRequireNonRepudiation());
        assertTrue(workflow.getRequiredExtendedKeyUsageOids().isEmpty());
    }

    @Test
    void theFamilyAndLevelTravelAsTheirWireCodes() throws Exception {
        String json = mapper.writeValueAsString(fullyConfiguredRequest());

        assertTrue(json.contains("\"family\":\"pades\""), json);
        assertTrue(json.contains("\"maxLevel\":\"archival\""), json);
    }

    @Test
    void theCertificatePurposeConstraintsDefaultToTheUntightenedRule() {
        ContentSigningWorkflowRequestDto request = new ContentSigningWorkflowRequestDto();

        assertNull(request.getRequireNonRepudiation());
        assertTrue(request.getRequiredExtendedKeyUsageOids().isEmpty());
    }

    @Test
    void anEkuEntryThatIsNotAnOidIsRejected() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request.setRequiredExtendedKeyUsageOids(Set.of("id-kp-documentSigning"));

        Set<ConstraintViolation<ContentSigningWorkflowRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size(), violations.toString());
        assertEquals("Invalid OID format", violations.iterator().next().getMessage());
    }

    @Test
    void aBlankEkuEntryIsRejected() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request.setRequiredExtendedKeyUsageOids(Set.of(" "));

        assertFalse(VALIDATOR.validate(request).isEmpty());
    }

    @Test
    void aWellFormedEkuOidPasses() {
        assertTrue(VALIDATOR.validate(fullyConfiguredRequest()).isEmpty());
    }

    @Test
    void anEkuOidWhoseSecondArcExceedsItsRootIsRejected() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request.setRequiredExtendedKeyUsageOids(Set.of("1.40.1"));

        assertFalse(VALIDATOR.validate(request).isEmpty());
    }

    @Test
    void anEkuOidUnderTheUnrestrictedRootIsAccepted() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request.setRequiredExtendedKeyUsageOids(Set.of("2.999.1", "1.39.1"));

        assertTrue(VALIDATOR.validate(request).isEmpty());
    }
}

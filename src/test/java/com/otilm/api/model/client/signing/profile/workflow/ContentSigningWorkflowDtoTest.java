package com.otilm.api.model.client.signing.profile.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.InternalTimestampSourceDto;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.InternalTimestampSourceRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceType;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
        dto.setFamily(SignatureFamily.PADES);
        dto.setMaxLevel(SignatureLevel.ARCHIVAL);
        dto.setTimestampSource(new InternalTimestampSourceRequestDto(UUID.randomUUID()));
        dto.setDocumentSizeCap(5_242_880L);
        dto.setRequireNonRepudiation(true);
        dto.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));
        return dto;
    }

    private static PadesSignatureParametersDto padesDefaults() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setReason("Contract approval");
        parameters.setLocation("Prague");
        return parameters;
    }

    /**
     * The allow-list and the defaults are orthogonal, so the round-trip carries a group with no default and vice versa.
     */
    @Test
    void theRequestRoundTripsTheAllowListAndTheTypedDefaults() throws Exception {
        ContentSigningWorkflowRequestDto original = fullyConfiguredRequest();
        original
                .setAllowedRequestParameterGroups(
                        new LinkedHashSet<>(Set.of(SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT)));
        original.setDefaultSignatureParameters(padesDefaults());

        String json = mapper.writeValueAsString(original);
        WorkflowRequestDto decoded = mapper.readValue(json, WorkflowRequestDto.class);

        ContentSigningWorkflowRequestDto workflow = assertInstanceOf(ContentSigningWorkflowRequestDto.class, decoded);
        assertEquals(Set.of(SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT),
                workflow.getAllowedRequestParameterGroups());
        PadesSignatureParametersDto defaults = assertInstanceOf(PadesSignatureParametersDto.class,
                workflow.getDefaultSignatureParameters());
        assertEquals(SignatureFamily.PADES, defaults.getFamily());
        assertEquals("Contract approval", defaults.getReason());
        assertEquals("Prague", defaults.getLocation());
    }

    @Test
    void theResponseRoundTripsTheAllowListAndTheTypedDefaults() throws Exception {
        ContentSigningWorkflowDto original = new ContentSigningWorkflowDto();
        original
                .setAllowedRequestParameterGroups(
                        new LinkedHashSet<>(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT)));
        original.setDefaultSignatureParameters(padesDefaults());

        String json = mapper.writeValueAsString(original);
        WorkflowDto decoded = mapper.readValue(json, WorkflowDto.class);

        ContentSigningWorkflowDto workflow = assertInstanceOf(ContentSigningWorkflowDto.class, decoded);
        assertEquals(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT), workflow.getAllowedRequestParameterGroups());
        PadesSignatureParametersDto defaults = assertInstanceOf(PadesSignatureParametersDto.class,
                workflow.getDefaultSignatureParameters());
        assertEquals(SignatureFamily.PADES, defaults.getFamily());
        assertEquals("Contract approval", defaults.getReason());
        assertEquals("Prague", defaults.getLocation());
    }

    @Test
    void theValidCascadeAppliesTheRequestFieldCapsToTheDefaults() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        PadesSignatureParametersDto defaults = new PadesSignatureParametersDto();
        defaults.setReason("x".repeat(513));
        request.setDefaultSignatureParameters(defaults);

        Set<ConstraintViolation<ContentSigningWorkflowRequestDto>> violations = VALIDATOR.validate(request);

        assertFalse(violations.isEmpty(), "an over-long default reason passed the cascade");
        assertTrue(
                violations
                        .stream()
                        .anyMatch(v -> "defaultSignatureParameters.reason".equals(v.getPropertyPath().toString())),
                "the violation does not point at the offending default field");
    }

    @Test
    void theRequestCarryingTypedDefaultsSurvivesJavaSerialization() throws Exception {
        ContentSigningWorkflowRequestDto original = fullyConfiguredRequest();
        original
                .setAllowedRequestParameterGroups(
                        new LinkedHashSet<>(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT)));
        original.setDefaultSignatureParameters(padesDefaults());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(original);
        }
        ContentSigningWorkflowRequestDto restored;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = assertInstanceOf(ContentSigningWorkflowRequestDto.class, in.readObject());
        }

        assertEquals(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT), restored.getAllowedRequestParameterGroups());
        assertEquals("Contract approval",
                assertInstanceOf(PadesSignatureParametersDto.class, restored.getDefaultSignatureParameters())
                        .getReason());
    }

    @Test
    void theResponseCarryingTypedDefaultsSurvivesJavaSerialization() throws Exception {
        ContentSigningWorkflowDto original = new ContentSigningWorkflowDto();
        original.setFamily(SignatureFamily.PADES);
        original.setMaxLevel(SignatureLevel.ARCHIVAL);
        original.setSignatureFormattingConnector(new NameAndUuidDto(UUID.randomUUID(), "Formatter"));
        original.setTimestampSource(new InternalTimestampSourceDto(new NameAndUuidDto(UUID.randomUUID(), "TSA")));
        original
                .setAllowedRequestParameterGroups(
                        new LinkedHashSet<>(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT)));
        original.setDefaultSignatureParameters(padesDefaults());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(original);
        }
        ContentSigningWorkflowDto restored;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = assertInstanceOf(ContentSigningWorkflowDto.class, in.readObject());
        }

        assertEquals(Set.of(SignatureParameterGroup.SIGNATURE_CONTEXT), restored.getAllowedRequestParameterGroups());
        assertEquals("Contract approval",
                assertInstanceOf(PadesSignatureParametersDto.class, restored.getDefaultSignatureParameters())
                        .getReason());
    }

    @Test
    void aNullGroupInTheAllowListIsRejected() {
        ContentSigningWorkflowRequestDto request = fullyConfiguredRequest();
        request
                .setAllowedRequestParameterGroups(
                        new LinkedHashSet<>(Arrays.asList(SignatureParameterGroup.SIGNATURE_CONTEXT, null)));

        Set<ConstraintViolation<ContentSigningWorkflowRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size(), violations.toString());
        assertEquals("allowedRequestParameterGroups[].<iterable element>",
                violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void theAllowListStartsEmptyRatherThanNullOnBothSurfaces() {
        assertEquals(Set.of(), new ContentSigningWorkflowRequestDto().getAllowedRequestParameterGroups());
        assertEquals(Set.of(), new ContentSigningWorkflowDto().getAllowedRequestParameterGroups());
    }

    @Test
    void theTypedDefaultsStartNullOnBothSurfaces() {
        assertNull(new ContentSigningWorkflowRequestDto().getDefaultSignatureParameters());
        assertNull(new ContentSigningWorkflowDto().getDefaultSignatureParameters());
    }

    @Test
    void theRequestRoundTripsThroughTheWorkflowUnion() throws Exception {
        ContentSigningWorkflowRequestDto original = fullyConfiguredRequest();

        String json = mapper.writeValueAsString(original);
        WorkflowRequestDto decoded = mapper.readValue(json, WorkflowRequestDto.class);

        ContentSigningWorkflowRequestDto workflow = assertInstanceOf(ContentSigningWorkflowRequestDto.class, decoded);
        assertEquals(SigningWorkflowType.CONTENT_SIGNING, workflow.getType());
        assertEquals(SignatureFamily.PADES, workflow.getFamily());
        assertEquals(SignatureLevel.ARCHIVAL, workflow.getMaxLevel());
        assertEquals(5_242_880L, workflow.getDocumentSizeCap());
        assertEquals(TimestampSourceType.INTERNAL, workflow.getTimestampSource().getType());
        assertEquals(original.getTimestampSource(), workflow.getTimestampSource());
        assertEquals(Boolean.TRUE, workflow.getRequireNonRepudiation());
        assertEquals(Set.of("1.3.6.1.5.5.7.3.36"), workflow.getRequiredExtendedKeyUsageOids());
    }

    @Test
    void theResponseRoundTripsThroughTheWorkflowUnion() throws Exception {
        ContentSigningWorkflowDto original = new ContentSigningWorkflowDto();
        original.setFamily(SignatureFamily.XADES);
        original.setMaxLevel(SignatureLevel.TIMESTAMPED);
        original.setDocumentSizeCap(1024L);
        original.setTimestampSource(new InternalTimestampSourceDto(new NameAndUuidDto(UUID.randomUUID(), "tsa")));

        WorkflowDto decoded = mapper.readValue(mapper.writeValueAsString(original), WorkflowDto.class);

        ContentSigningWorkflowDto workflow = assertInstanceOf(ContentSigningWorkflowDto.class, decoded);
        assertEquals(SignatureFamily.XADES, workflow.getFamily());
        assertEquals(SignatureLevel.TIMESTAMPED, workflow.getMaxLevel());
        assertEquals(1024L, workflow.getDocumentSizeCap());
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
        assertNull(workflow.getDocumentSizeCap());
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

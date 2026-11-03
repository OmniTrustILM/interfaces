package com.otilm.api.model.client.signing.profile.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RawSigningWorkflowDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void everyRawFieldIsOptionalOnTheWire() throws Exception {
        WorkflowRequestDto decoded = mapper.readValue("{\"type\":\"raw_signing\"}", WorkflowRequestDto.class);

        RawSigningWorkflowRequestDto workflow = assertInstanceOf(RawSigningWorkflowRequestDto.class, decoded);
        assertNull(workflow.getMaxDigestsPerRequest());
        assertNull(workflow.getRequireNonRepudiation());
        assertTrue(workflow.getRequiredExtendedKeyUsageOids().isEmpty());
    }

    @Test
    void anEmptyRequestValidatesBecauseAnAbsentCapTakesThePlatformDefault() {
        Set<ConstraintViolation<RawSigningWorkflowRequestDto>> violations = VALIDATOR
                .validate(new RawSigningWorkflowRequestDto());

        assertTrue(violations.isEmpty(), "unexpected violations: " + violations);
    }

    @Test
    void theRequestRoundTripsThroughTheWorkflowUnion() throws Exception {
        RawSigningWorkflowRequestDto original = new RawSigningWorkflowRequestDto();
        original.setMaxDigestsPerRequest(100);
        original.setRequireNonRepudiation(true);
        original.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));

        WorkflowRequestDto decoded = mapper.readValue(mapper.writeValueAsString(original), WorkflowRequestDto.class);

        RawSigningWorkflowRequestDto workflow = assertInstanceOf(RawSigningWorkflowRequestDto.class, decoded);
        assertEquals(SigningWorkflowType.RAW_SIGNING, workflow.getType());
        assertEquals(100, workflow.getMaxDigestsPerRequest());
        assertEquals(Boolean.TRUE, workflow.getRequireNonRepudiation());
        assertEquals(Set.of("1.3.6.1.5.5.7.3.36"), workflow.getRequiredExtendedKeyUsageOids());
    }

    @Test
    void theResponseRoundTripsThroughTheWorkflowUnion() throws Exception {
        RawSigningWorkflowDto original = new RawSigningWorkflowDto();
        original.setMaxDigestsPerRequest(100);
        original.setRequireNonRepudiation(true);
        original.setRequiredExtendedKeyUsageOids(Set.of("1.3.6.1.5.5.7.3.36"));

        WorkflowDto decoded = mapper.readValue(mapper.writeValueAsString(original), WorkflowDto.class);

        RawSigningWorkflowDto workflow = assertInstanceOf(RawSigningWorkflowDto.class, decoded);
        assertEquals(100, workflow.getMaxDigestsPerRequest());
        assertTrue(workflow.isRequireNonRepudiation());
        assertEquals(Set.of("1.3.6.1.5.5.7.3.36"), workflow.getRequiredExtendedKeyUsageOids());
    }

    @Test
    void aZeroDigestCapIsRejected() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setMaxDigestsPerRequest(0);

        Set<ConstraintViolation<RawSigningWorkflowRequestDto>> violations = VALIDATOR.validate(workflow);

        assertEquals(1, violations.size(), violations.toString());
        assertEquals("maxDigestsPerRequest", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void aDigestCapOfOneIsAccepted() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setMaxDigestsPerRequest(1);

        assertTrue(VALIDATOR.validate(workflow).isEmpty());
    }

    @Test
    void anEkuEntryThatIsNotAnOidIsRejected() {
        RawSigningWorkflowRequestDto workflow = new RawSigningWorkflowRequestDto();
        workflow.setRequiredExtendedKeyUsageOids(Set.of("id-kp-documentSigning"));

        Set<ConstraintViolation<RawSigningWorkflowRequestDto>> violations = VALIDATOR.validate(workflow);

        assertEquals(1, violations.size(), violations.toString());
        assertEquals("Invalid OID format", violations.iterator().next().getMessage());
    }

    @Test
    void noDigestAlgorithmListIsPublished() {
        assertThrows(NoSuchFieldException.class,
                () -> RawSigningWorkflowRequestDto.class.getDeclaredField("allowedDigestAlgorithms"));
        assertThrows(NoSuchFieldException.class,
                () -> RawSigningWorkflowDto.class.getDeclaredField("allowedDigestAlgorithms"));
    }
}

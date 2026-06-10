package com.otilm.api.model.client.signing.profile.record.validation;

import com.otilm.api.model.client.signing.profile.SigningProfileRequestDto;
import com.otilm.api.model.client.signing.profile.record.SigningRecordPolicyRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.RawSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningRecordPolicyValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private SigningProfileRequestDto baseRequest() {
        SigningProfileRequestDto r = new SigningProfileRequestDto();
        r.setName("p");
        return r;
    }

    @Test
    void recordSignedDocumentRejectedForRawSigning() {
        SigningProfileRequestDto r = baseRequest();
        r.setWorkflow(new RawSigningWorkflowRequestDto());
        SigningRecordPolicyRequestDto policy = new SigningRecordPolicyRequestDto();
        policy.setRecordSignedDocument(true);
        r.setRecordPolicy(policy);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator.validate(r);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().contains("recordSignedDocument")));
    }

    @Test
    void recordSignedDocumentAcceptedForContentSigning() {
        SigningProfileRequestDto r = baseRequest();
        r.setWorkflow(new ContentSigningWorkflowRequestDto());
        SigningRecordPolicyRequestDto policy = new SigningRecordPolicyRequestDto();
        policy.setRecordSignedDocument(true);
        r.setRecordPolicy(policy);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator.validate(r);

        assertTrue(violations.stream().noneMatch(v ->
                v.getPropertyPath().toString().contains("recordSignedDocument")));
    }

    @Test
    void recordSignedDocumentAcceptedForTimestamping() {
        SigningProfileRequestDto r = baseRequest();
        r.setWorkflow(new TimestampingWorkflowRequestDto());
        SigningRecordPolicyRequestDto policy = new SigningRecordPolicyRequestDto();
        policy.setRecordSignedDocument(true);
        r.setRecordPolicy(policy);

        Set<ConstraintViolation<SigningProfileRequestDto>> violations = validator.validate(r);

        assertTrue(violations.stream().noneMatch(v ->
                v.getPropertyPath().toString().contains("recordSignedDocument")));
    }
}

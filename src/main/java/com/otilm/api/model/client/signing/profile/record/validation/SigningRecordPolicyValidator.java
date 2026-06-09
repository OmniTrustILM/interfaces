package com.otilm.api.model.client.signing.profile.record.validation;

import com.otilm.api.model.client.signing.profile.SigningProfileRequestDto;
import com.otilm.api.model.client.signing.profile.record.SigningRecordPolicyRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.WorkflowRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SigningRecordPolicyValidator
        implements ConstraintValidator<ValidSigningRecordPolicy, SigningProfileRequestDto> {

    @Override
    public boolean isValid(SigningProfileRequestDto dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.getRecordPolicy() == null) {
            return true;
        }

        SigningRecordPolicyRequestDto policy = dto.getRecordPolicy();
        if (!policy.isRecordSignedDocument()) {
            return true;
        }

        WorkflowRequestDto wf = dto.getWorkflow();
        if (wf == null) {
            return true;
        }

        boolean isSupportedWorkflow = wf instanceof ContentSigningWorkflowRequestDto
                  || wf instanceof TimestampingWorkflowRequestDto;
        if (!isSupportedWorkflow) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("recordPolicy").addPropertyNode("recordSignedDocument")
                    .addConstraintViolation();
        }
        return isSupportedWorkflow;
    }
}

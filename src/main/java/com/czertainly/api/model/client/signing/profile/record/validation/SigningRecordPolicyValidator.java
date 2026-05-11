package com.czertainly.api.model.client.signing.profile.record.validation;

import com.czertainly.api.model.client.signing.profile.SigningProfileRequestDto;
import com.czertainly.api.model.client.signing.profile.record.SigningRecordPolicyRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.WorkflowRequestDto;
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
        boolean ok = wf instanceof ContentSigningWorkflowRequestDto
                  || wf instanceof TimestampingWorkflowRequestDto;
        if (!ok) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("recordPolicy").addPropertyNode("recordSignedDocument")
                    .addConstraintViolation();
        }
        return ok;
    }
}

package com.czertainly.api.model.client.signing.profile.workflow.validation;

import com.czertainly.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DefaultPolicyIdValidator implements ConstraintValidator<ValidDefaultPolicyId, TimestampingWorkflowRequestDto> {

    @Override
    public boolean isValid(TimestampingWorkflowRequestDto workflow, ConstraintValidatorContext context) {
        if (workflow == null
                || workflow.getDefaultPolicyId() == null
                || workflow.getAllowedPolicyIds() == null
                || workflow.getAllowedPolicyIds().isEmpty()) {
            return true;
        }
        if (workflow.getAllowedPolicyIds().contains(workflow.getDefaultPolicyId())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("defaultPolicyId")
                .addConstraintViolation();
        return false;
    }
}

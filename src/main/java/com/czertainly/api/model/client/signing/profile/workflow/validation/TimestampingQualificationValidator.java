package com.czertainly.api.model.client.signing.profile.workflow.validation;

import com.czertainly.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimestampingQualificationValidator implements ConstraintValidator<ValidTimestampingQualification, TimestampingWorkflowRequestDto> {

    @Override
    public boolean isValid(TimestampingWorkflowRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        if (Boolean.TRUE.equals(dto.getQualifiedTimestamp()) && dto.getTimeQualityConfigurationUuid() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("timeQualityConfigurationUuid")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

package com.czertainly.api.model.core.signing.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QualifiedTimestampValidator
        implements ConstraintValidator<ValidQualifiedTimestamp, QualifiedTimestampConstrained> {

    @Override
    public boolean isValid(QualifiedTimestampConstrained dto,
                           ConstraintValidatorContext context) {
        // null object — let @NotNull on the DTO handle that separately
        if (dto == null) {
            return true;
        }
        if (Boolean.TRUE.equals(dto.getQualifiedTimestamp())) {
            return dto.getTimeQualityConfigurationUuid() != null;
        }
        return true;
    }
}

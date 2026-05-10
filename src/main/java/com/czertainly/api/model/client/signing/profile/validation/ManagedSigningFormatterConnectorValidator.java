package com.czertainly.api.model.client.signing.profile.validation;

import com.czertainly.api.model.client.signing.profile.SigningProfileRequestDto;
import com.czertainly.api.model.client.signing.profile.scheme.ManagedSigningRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class ManagedSigningFormatterConnectorValidator implements ConstraintValidator<ValidManagedSigningFormatterConnector, SigningProfileRequestDto> {

    @Override
    public boolean isValid(SigningProfileRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getSigningScheme() == null || dto.getWorkflow() == null) {
            return true;
        }
        if (!(dto.getSigningScheme() instanceof ManagedSigningRequestDto)) {
            return true;
        }

        boolean valid = true;

        UUID formatterConnectorUuid = null;

        if (dto.getWorkflow() instanceof TimestampingWorkflowRequestDto tsw) {
            formatterConnectorUuid = tsw.getSignatureFormatterConnectorUuid();
            if (Boolean.TRUE.equals(tsw.getQualifiedTimestamp()) && tsw.getTimeQualityConfigurationUuid() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("timeQualityConfigurationUuid must be provided when qualifiedTimestamp is true")
                        .addPropertyNode("workflow.timeQualityConfigurationUuid")
                        .addConstraintViolation();
                valid = false;
            }
        } else if (dto.getWorkflow() instanceof ContentSigningWorkflowRequestDto csw) {
            formatterConnectorUuid = csw.getSignatureFormatterConnectorUuid();
        } else {
            return true;
        }

        if (formatterConnectorUuid == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("workflow.signatureFormatterConnectorUuid")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}

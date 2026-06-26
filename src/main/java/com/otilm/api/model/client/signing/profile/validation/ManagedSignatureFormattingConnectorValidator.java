package com.otilm.api.model.client.signing.profile.validation;

import com.otilm.api.model.client.signing.profile.SigningProfileRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class ManagedSignatureFormattingConnectorValidator implements ConstraintValidator<ValidManagedSignatureFormattingConnector, SigningProfileRequestDto> {

    @Override
    public boolean isValid(SigningProfileRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getSigningScheme() == null || dto.getWorkflow() == null) {
            return true;
        }
        if (!(dto.getSigningScheme() instanceof ManagedSigningRequestDto)) {
            return true;
        }

        boolean valid = true;

        UUID formattingConnectorUuid = null;

        if (dto.getWorkflow() instanceof TimestampingWorkflowRequestDto tsw) {
            formattingConnectorUuid = tsw.getSignatureFormattingConnectorUuid();
            if (Boolean.TRUE.equals(tsw.getQualifiedTimestamp()) && tsw.getTimeQualityConfigurationUuid() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("timeQualityConfigurationUuid must be provided when qualifiedTimestamp is true")
                        .addPropertyNode("workflow").addPropertyNode("timeQualityConfigurationUuid")
                        .addConstraintViolation();
                valid = false;
            }
        } else if (dto.getWorkflow() instanceof ContentSigningWorkflowRequestDto csw) {
            formattingConnectorUuid = csw.getSignatureFormattingConnectorUuid();
        } else {
            return true;
        }

        if (formattingConnectorUuid == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("workflow").addPropertyNode("signatureFormattingConnectorUuid")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}

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

        UUID formatterUuid = null;
        String propertyNode = null;

        if (dto.getWorkflow() instanceof TimestampingWorkflowRequestDto tsw) {
            formatterUuid = tsw.getSignatureFormatterConnectorUuid();
            propertyNode = "workflow.signatureFormatterConnectorUuid";
        } else if (dto.getWorkflow() instanceof ContentSigningWorkflowRequestDto csw) {
            formatterUuid = csw.getSignatureFormatterConnectorUuid();
            propertyNode = "workflow.signatureFormatterConnectorUuid";
        } else {
            return true;
        }

        if (formatterUuid == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(propertyNode)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

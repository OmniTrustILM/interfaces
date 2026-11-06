package com.otilm.api.model.client.signing.profile.validation;

import com.otilm.api.model.client.signing.profile.SigningProfileRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.ContentSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.RawSigningWorkflowRequestDto;
import com.otilm.api.model.client.signing.profile.workflow.TimestampingWorkflowRequestDto;
import com.otilm.api.model.common.signature.SignatureLevel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.UUID;

public class ManagedSignatureFormattingConnectorValidator
        implements
            ConstraintValidator<ValidManagedSignatureFormattingConnector, SigningProfileRequestDto> {

    private static final String WORKFLOW_NODE = "workflow";
    private static final String TIMESTAMP_SOURCE_PROPERTY = "timestampSource";

    @Override
    public boolean isValid(SigningProfileRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getSigningScheme() == null || dto.getWorkflow() == null) {
            return true;
        }
        if (!(dto.getSigningScheme() instanceof ManagedSigningRequestDto)) {
            return validateDelegatedWorkflow(dto, context);
        }

        boolean valid = true;

        UUID formattingConnectorUuid = null;

        if (dto.getWorkflow() instanceof TimestampingWorkflowRequestDto tsw) {
            formattingConnectorUuid = tsw.getSignatureFormattingConnectorUuid();
            if (Boolean.TRUE.equals(tsw.getQualifiedTimestamp()) && tsw.getTimeQualityConfigurationUuid() == null) {
                addWorkflowViolation(context, "timeQualityConfigurationUuid",
                        "timeQualityConfigurationUuid must be provided when qualifiedTimestamp is true");
                valid = false;
            }
        } else if (dto.getWorkflow() instanceof ContentSigningWorkflowRequestDto dsw) {
            formattingConnectorUuid = dsw.getSignatureFormattingConnectorUuid();
            boolean familyPresent = requireOnManagedWorkflow(context, "family", dsw.getFamily());
            boolean maxLevelPresent = requireOnManagedWorkflow(context, "maxLevel", dsw.getMaxLevel());
            boolean sourceConsistent = validateTimestampSource(context, dsw);
            valid = familyPresent && maxLevelPresent && sourceConsistent;
        } else {
            return true;
        }

        if (formattingConnectorUuid == null) {
            addWorkflowViolation(context, "signatureFormattingConnectorUuid",
                    context.getDefaultConstraintMessageTemplate());
            valid = false;
        }
        return valid;
    }

    /**
     * Levels above SIGNED embed at least one timestamp, so the profile has to say where those timestamps come from, and
     * SIGNED embeds none, so a source there would never be invoked. Both directions are rejected for the same reason:
     * the profile must not store configuration the platform does not honour. The check lives here rather than on the
     * workflow itself because it applies to ILM-managed signing only.
     */
    private boolean validateTimestampSource(ConstraintValidatorContext context,
            ContentSigningWorkflowRequestDto workflow) {
        if (workflow.getMaxLevel() == null) {
            return true;
        }
        boolean sourcePresent = workflow.getTimestampSource() != null;
        if (workflow.getMaxLevel() == SignatureLevel.SIGNED) {
            if (!sourcePresent) {
                return true;
            }
            addWorkflowViolation(context, TIMESTAMP_SOURCE_PROPERTY,
                    "timestampSource must be omitted when maxLevel is SIGNED");
            return false;
        }
        if (sourcePresent) {
            return true;
        }
        addWorkflowViolation(context, TIMESTAMP_SOURCE_PROPERTY,
                "timestampSource is required when maxLevel is TIMESTAMPED or higher");
        return false;
    }

    /**
     * Delegated signing formats the signature outside the platform, so the ILM-managed fields have no effect. Rejecting
     * them names the mistake at the field the caller set, rather than storing configuration the platform never honours.
     */
    private boolean validateDelegatedWorkflow(SigningProfileRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getWorkflow() instanceof RawSigningWorkflowRequestDto raw) {
            return validateDelegatedRawWorkflow(context, raw);
        }
        if (!(dto.getWorkflow() instanceof ContentSigningWorkflowRequestDto workflow)) {
            return true;
        }
        boolean connectorAbsent = rejectOnDelegatedWorkflow(context, "signatureFormattingConnectorUuid",
                workflow.getSignatureFormattingConnectorUuid());
        boolean attributesAbsent = rejectOnDelegatedWorkflow(context, "signatureFormattingConnectorAttributes",
                workflow.getSignatureFormattingConnectorAttributes());
        boolean familyAbsent = rejectOnDelegatedWorkflow(context, "family", workflow.getFamily());
        boolean maxLevelAbsent = rejectOnDelegatedWorkflow(context, "maxLevel", workflow.getMaxLevel());
        boolean sourceAbsent = rejectOnDelegatedWorkflow(context, TIMESTAMP_SOURCE_PROPERTY,
                workflow.getTimestampSource());
        boolean nonRepudiationAbsent = rejectTrueOnDelegatedWorkflow(context, "requireNonRepudiation",
                workflow.getRequireNonRepudiation());
        boolean extendedKeyUsageAbsent = rejectOnDelegatedWorkflow(context, "requiredExtendedKeyUsageOids",
                workflow.getRequiredExtendedKeyUsageOids());
        return connectorAbsent && attributesAbsent && familyAbsent && maxLevelAbsent && sourceAbsent
                && nonRepudiationAbsent && extendedKeyUsageAbsent;
    }

    private boolean validateDelegatedRawWorkflow(ConstraintValidatorContext context,
            RawSigningWorkflowRequestDto workflow) {
        boolean nonRepudiationAbsent = rejectTrueOnDelegatedWorkflow(context, "requireNonRepudiation",
                workflow.getRequireNonRepudiation());
        boolean extendedKeyUsageAbsent = rejectOnDelegatedWorkflow(context, "requiredExtendedKeyUsageOids",
                workflow.getRequiredExtendedKeyUsageOids());
        return nonRepudiationAbsent && extendedKeyUsageAbsent;
    }

    private boolean rejectOnDelegatedWorkflow(ConstraintValidatorContext context, String property,
            Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return true;
        }
        return rejectOnDelegatedWorkflow(context, property, (Object) values);
    }

    /**
     * An explicit {@code false} asks for no constraint and is tolerated.
     */
    private boolean rejectTrueOnDelegatedWorkflow(ConstraintValidatorContext context, String property, Boolean value) {
        if (!Boolean.TRUE.equals(value)) {
            return true;
        }
        return rejectOnDelegatedWorkflow(context, property, (Object) value);
    }

    private boolean rejectOnDelegatedWorkflow(ConstraintValidatorContext context, String property, Object value) {
        if (value == null) {
            return true;
        }
        addWorkflowViolation(context, property, property + " must be omitted for delegated signing");
        return false;
    }

    private boolean requireOnManagedWorkflow(ConstraintValidatorContext context, String property, Object value) {
        if (value != null) {
            return true;
        }
        addWorkflowViolation(context, property, property + " is required for ILM-managed signing");
        return false;
    }

    /** Every violation this validator raises names the offending workflow property, so they all share one shape. */
    private void addWorkflowViolation(ConstraintValidatorContext context, String property, String message) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(WORKFLOW_NODE)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}

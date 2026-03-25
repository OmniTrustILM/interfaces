package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * OpenAPI schema interface for the polymorphic {@link WorkflowDto} hierarchy.
 *
 * <p>Workflow configuration captures all signing-workflow-type-specific properties
 * embedded directly inside a {@code SignatureProfile}. The concrete subtype is
 * determined by the {@code type} discriminator ({@link SigningWorkflowType}).</p>
 *
 * <p>{@code RAW_SIGNING} carries no additional properties; its concrete subtype
 * {@link RawSigningWorkflowDto} holds only the {@code type} discriminator,
 * ensuring {@code workflowConfiguration} is never {@code null} on a Signature Profile.</p>
 */
@Schema(
        name = "WorkflowInterface",
        description = "Workflow configuration specific to the signing workflow type, embedded in a Signature Profile",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.TIMESTAMPING, schema = TimestampingWorkflowDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.DOCUMENT_SIGNING, schema = DocumentSigningWorkflowDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.CODE_BINARY_SIGNING, schema = CodeBinarySigningWorkflowDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.RAW_SIGNING, schema = RawSigningWorkflowDto.class),
        },
        oneOf = {
                TimestampingWorkflowDto.class,
                DocumentSigningWorkflowDto.class,
                CodeBinarySigningWorkflowDto.class,
                RawSigningWorkflowDto.class,
        })
public interface WorkflowInterface extends Serializable {

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    SigningWorkflowType getType();
}

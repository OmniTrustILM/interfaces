package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * OpenAPI schema interface for the polymorphic {@link WorkflowRequestDto} hierarchy.
 *
 * <p>Used for both create and update requests; the structure of workflow configuration
 * is identical in both operations since it is embedded inside the Signature Profile request.</p>
 *
 * <p>{@code RAW_SIGNING} carries no additional properties — use {@link RawSigningWorkflowRequestDto}
 * (body: {@code {"type":"raw_signing"}}) to make the workflow type explicit.
 * The {@code workflowConfiguration} field must never be {@code null} on a Signature Profile request.</p>
 */
@Schema(
        name = "WorkflowRequestInterface",
        description = "Workflow configuration request specific to the signing workflow type",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.TIMESTAMPING, schema = TimestampingWorkflowRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.DOCUMENT_SIGNING, schema = DocumentSigningWorkflowRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.CODE_BINARY_SIGNING, schema = CodeBinarySigningWorkflowRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.RAW_SIGNING, schema = RawSigningWorkflowRequestDto.class),
        },
        oneOf = {
                TimestampingWorkflowRequestDto.class,
                DocumentSigningWorkflowRequestDto.class,
                CodeBinarySigningWorkflowRequestDto.class,
                RawSigningWorkflowRequestDto.class,
        })
public interface WorkflowRequestInterface extends Serializable {

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    SigningWorkflowType getType();
}

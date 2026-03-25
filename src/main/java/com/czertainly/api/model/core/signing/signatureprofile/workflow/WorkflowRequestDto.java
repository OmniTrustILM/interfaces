package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Abstract base for all signing-workflow-type-specific configuration request DTOs
 * embedded inside Signature Profile create/update requests.
 *
 * <p>This single request base covers both create and update operations; the structure of
 * workflow configuration is identical in both cases.</p>
 *
 * <p>{@code RAW_SIGNING} carries no additional properties — use {@link RawSigningWorkflowRequestDto}
 * (body: {@code {"type":"raw_signing"}}) to make the workflow type explicit. The {@code workflowConfiguration}
 * field on the Signature Profile request must never be {@code null}.</p>
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampingWorkflowRequestDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
        @JsonSubTypes.Type(value = DocumentSigningWorkflowRequestDto.class, name = SigningWorkflowType.Codes.DOCUMENT_SIGNING),
        @JsonSubTypes.Type(value = CodeBinarySigningWorkflowRequestDto.class, name = SigningWorkflowType.Codes.CODE_BINARY_SIGNING),
        @JsonSubTypes.Type(value = RawSigningWorkflowRequestDto.class, name = SigningWorkflowType.Codes.RAW_SIGNING),
})
@Schema(implementation = WorkflowRequestInterface.class)
public abstract class WorkflowRequestDto implements WorkflowRequestInterface {

    @NotNull
    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    private final SigningWorkflowType type;

    protected WorkflowRequestDto(SigningWorkflowType type) {
        this.type = type;
    }
}

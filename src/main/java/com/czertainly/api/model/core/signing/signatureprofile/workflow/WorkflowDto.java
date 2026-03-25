package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Abstract base for all signing-workflow-type-specific configuration response DTOs
 * embedded inside a {@code SignatureProfileDto}.
 *
 * <p>Workflow configuration is split into two logical categories (expressed as flat fields
 * on each concrete subtype):</p>
 * <ul>
 *   <li><b>Signature Formatter Connector properties</b> — the connector UUID and its
 *       dynamically fetched attributes that control construction of the data-to-be-signed (DTBS).
 *       Present only for ILM-managed signing; absent (null) for delegated signing and for
 *       {@code RAW_SIGNING} (which requires no DTBS formatting).</li>
 *   <li><b>Workflow validation properties</b> — properties used by the platform to validate
 *       signing or timestamping operations (e.g. TSA Policy IDs). Some of these apply to both
 *       managed and delegated signing schemes; others are managed-only (documented per field).</li>
 * </ul>
 *
 * <p>{@code RAW_SIGNING} carries no additional properties — its concrete subtype
 * {@link RawSigningWorkflowDto} holds only the {@code type} discriminator,
 * ensuring {@code workflowConfiguration} is never {@code null} in a Signature Profile.</p>
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampingWorkflowDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
        @JsonSubTypes.Type(value = DocumentSigningWorkflowDto.class, name = SigningWorkflowType.Codes.DOCUMENT_SIGNING),
        @JsonSubTypes.Type(value = CodeBinarySigningWorkflowDto.class, name = SigningWorkflowType.Codes.CODE_BINARY_SIGNING),
        @JsonSubTypes.Type(value = RawSigningWorkflowDto.class, name = SigningWorkflowType.Codes.RAW_SIGNING),
})
@Schema(implementation = WorkflowInterface.class)
public abstract class WorkflowDto implements WorkflowInterface {

    @NotNull
    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    private final SigningWorkflowType type;

    protected WorkflowDto(SigningWorkflowType type) {
        this.type = type;
    }
}

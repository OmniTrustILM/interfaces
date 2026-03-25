package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Raw signing workflow configuration embedded in a {@code SignatureProfileDto}.
 *
 * <p>Raw signing requires no Signature Formatter Connector and currently has no workflow-specific validation properties.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "RawSigningWorkflowDto", description = "Raw signing workflow configuration.")
@ToString(callSuper = true)
public class RawSigningWorkflowDto extends WorkflowDto {

    public RawSigningWorkflowDto() {
        super(SigningWorkflowType.RAW_SIGNING);
    }
}

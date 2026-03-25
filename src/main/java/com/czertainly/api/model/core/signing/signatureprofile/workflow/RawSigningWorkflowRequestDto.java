package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Raw signing workflow configuration request embedded in a Signature Profile create/update request.
 *
 * <p>Raw signing requires no Signature Formatter Connector and currently has no workflow-specific validation properties.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RawSigningWorkflowRequestDto", description = "Raw signing workflow configuration request.")
@ToString(callSuper = true)
public class RawSigningWorkflowRequestDto extends WorkflowRequestDto {

    public RawSigningWorkflowRequestDto() {
        super(SigningWorkflowType.RAW_SIGNING);
    }
}

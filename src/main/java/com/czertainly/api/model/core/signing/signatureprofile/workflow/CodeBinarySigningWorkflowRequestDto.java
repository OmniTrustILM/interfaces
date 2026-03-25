package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Code &amp; Binary signing workflow configuration request embedded in a Signature Profile create/update request.
 *
 * <p>Both fields apply to ILM-managed signing only and must be omitted (or set to null) when the Signature Profile uses delegated signing.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "CodeBinarySigningWorkflowRequestDto", description = "Code & Binary signing workflow configuration request")
@ToString(callSuper = true)
public class CodeBinarySigningWorkflowRequestDto extends WorkflowRequestDto {

    @Schema(
            description = "UUID of the Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Code & Binary signing. " +
                          "Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signatureFormatterConnectorUuid;

    @Schema(
            description = "Attributes for the Signature Formatter Connector that control DTBS construction " +
                          "for the code and binary signing workflow. " +
                          "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    public CodeBinarySigningWorkflowRequestDto() {
        super(SigningWorkflowType.CODE_BINARY_SIGNING);
    }
}

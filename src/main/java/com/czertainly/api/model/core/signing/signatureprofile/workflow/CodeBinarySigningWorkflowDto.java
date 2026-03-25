package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Code &amp; Binary signing workflow configuration embedded in a {@code SignatureProfileDto}.
 *
 * <p>Contains Signature Formatter Connector properties used by ILM-managed signing to construct the data-to-be-signed (DTBS)
 * for code and binary signing operations. Both fields are null when delegated signing is used.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "CodeBinarySigningWorkflowDto", description = "Code & Binary signing workflow configuration")
@ToString(callSuper = true)
public class CodeBinarySigningWorkflowDto extends WorkflowDto {

    @Schema(
            description = "Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Code & Binary signing. " +
                          "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto signatureFormatterConnector;

    @Schema(
            description = "Attributes configured on the Signature Formatter Connector that control DTBS construction " +
                          "for the code and binary signing workflow. " +
                          "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    public CodeBinarySigningWorkflowDto() {
        super(SigningWorkflowType.CODE_BINARY_SIGNING);
    }
}

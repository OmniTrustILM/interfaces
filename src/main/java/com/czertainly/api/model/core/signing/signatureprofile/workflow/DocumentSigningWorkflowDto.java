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
 * Document signing workflow configuration embedded in a {@code SignatureProfileDto}.
 *
 * <p>Contains Signature Formatter Connector properties used by ILM-managed signing to construct the data-to-be-signed (DTBS) for document signing operations.
 * Both fields are null when delegated signing is used.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "DocumentSigningWorkflowDto", description = "Document signing workflow configuration")
@ToString(callSuper = true)
public class DocumentSigningWorkflowDto extends WorkflowDto {

    @Schema(
            description = "Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Document signing. " +
                          "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto signatureFormatterConnector;

    @Schema(
            description = "Attributes configured on the Signature Formatter Connector that control DTBS construction " +
                          "for the document signing workflow. " +
                          "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    public DocumentSigningWorkflowDto() {
        super(SigningWorkflowType.DOCUMENT_SIGNING);
    }
}

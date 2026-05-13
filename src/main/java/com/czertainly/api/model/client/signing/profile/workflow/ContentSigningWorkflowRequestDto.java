package com.czertainly.api.model.client.signing.profile.workflow;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Content signing workflow configuration request embedded in a Signing Profile create/update request.
 *
 * <p>Both fields apply to ILM-managed signing only and must be omitted (or set to null) when the Signing Profile uses delegated signing.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ContentSigningWorkflowRequestDto", description = "Content signing workflow configuration request")
@ToString(callSuper = true)
public class ContentSigningWorkflowRequestDto extends WorkflowRequestDto {

    @Schema(
            description = "UUID of the Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Content signing. " +
                          "Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signatureFormatterConnectorUuid;

    @Schema(
            description = "Attributes for the Signature Formatter Connector that control DTBS construction " +
                          "for the content signing workflow. " +
                          "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    public ContentSigningWorkflowRequestDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}

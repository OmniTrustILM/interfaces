package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Content signing workflow configuration embedded in a {@code SigningProfileDto}.
 *
 * <p>Contains Signature Formatting Provider properties used by ILM-managed signing to construct the data-to-be-signed (DTBS) for content signing operations.
 * Both fields are null when delegated signing is used.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ContentSigningWorkflowDto", description = "Content signing workflow configuration")
@ToString(callSuper = true)
public class ContentSigningWorkflowDto extends WorkflowDto {

    @Schema(
            description = "Signature Formatting Provider that constructs the data-to-be-signed (DTBS) for Content signing. " +
                          "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto signatureFormattingConnector;

    @Schema(
            description = "Attributes configured on the Signature Formatting Provider that control DTBS construction " +
                          "for the content signing workflow. " +
                          "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureFormattingConnectorAttributes = new ArrayList<>();

    public ContentSigningWorkflowDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}

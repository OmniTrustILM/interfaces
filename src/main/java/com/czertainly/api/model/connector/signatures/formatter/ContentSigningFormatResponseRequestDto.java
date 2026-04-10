package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Response formatting request for the Content Signing workflow.
 *
 * <p>No additional fields beyond the base at this time.
 * Reserved for future content-signing-specific properties.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "ContentSigningFormatResponseRequestDto", description = "Response formatting request for Content Signing")
public class ContentSigningFormatResponseRequestDto extends FormatResponseRequestDto {

    public ContentSigningFormatResponseRequestDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}

package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * DTBS formatting request for the Document Signing workflow.
 *
 * <p>No additional Core-provided fields beyond the base at this time.
 * Reserved for future document-signing-specific properties.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "DocumentSigningFormatDtbsRequestDto", description = "DTBS formatting request for Document Signing")
public class DocumentSigningFormatDtbsRequestDto extends FormatDtbsRequestDto {

    public DocumentSigningFormatDtbsRequestDto() {
        super(SigningWorkflowType.DOCUMENT_SIGNING);
    }
}

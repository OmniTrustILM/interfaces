package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * DTBS formatting request for the Code &amp; Binary Signing workflow.
 *
 * <p>No additional Core-provided fields beyond the base at this time.
 * Reserved for future code/binary-signing-specific properties.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "CodeBinarySigningFormatDtbsRequestDto", description = "DTBS formatting request for Code & Binary Signing")
public class CodeBinarySigningFormatDtbsRequestDto extends FormatDtbsRequestDto {

    public CodeBinarySigningFormatDtbsRequestDto() {
        super(SigningWorkflowType.CODE_BINARY_SIGNING);
    }
}

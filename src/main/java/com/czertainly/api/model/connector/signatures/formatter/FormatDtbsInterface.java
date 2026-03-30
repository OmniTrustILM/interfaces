package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * OpenAPI schema interface for the polymorphic {@link FormatDtbsRequestDto} hierarchy.
 *
 * <p>The concrete subtype is determined by the {@code type} discriminator
 * ({@link SigningWorkflowType}). {@code RAW_SIGNING} is excluded — raw signing
 * does not invoke a Signature Formatter Connector.</p>
 */
@Schema(
        name = "FormatDtbsInterface",
        description = "DTBS formatting request specific to the signing workflow type",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.TIMESTAMPING, schema = TimestampingFormatDtbsRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.DOCUMENT_SIGNING, schema = DocumentSigningFormatDtbsRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.CODE_BINARY_SIGNING, schema = CodeBinarySigningFormatDtbsRequestDto.class),
        },
        oneOf = {
                TimestampingFormatDtbsRequestDto.class,
                DocumentSigningFormatDtbsRequestDto.class,
                CodeBinarySigningFormatDtbsRequestDto.class,
        })
public interface FormatDtbsInterface extends Serializable {

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    SigningWorkflowType getType();
}

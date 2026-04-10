package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * OpenAPI schema interface for the polymorphic {@link FormatResponseRequestDto} hierarchy.
 *
 * <p>The concrete subtype is determined by the {@code type} discriminator
 * ({@link SigningWorkflowType}). {@code RAW_SIGNING} is excluded — raw signing
 * does not invoke a Signature Formatter Connector.</p>
 */
@Schema(
        name = "FormatResponseInterface",
        description = "Response formatting request specific to the signing workflow type",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.TIMESTAMPING, schema = TimestampingFormatResponseRequestDto.class),
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.CONTENT_SIGNING, schema = ContentSigningFormatResponseRequestDto.class),
        },
        oneOf = {
                TimestampingFormatResponseRequestDto.class,
                ContentSigningFormatResponseRequestDto.class,
        })
public interface FormatResponseInterface extends Serializable {

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    SigningWorkflowType getType();
}

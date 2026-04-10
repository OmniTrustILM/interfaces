package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @Type(value = TimestampingFormatResponseRequestDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
        @Type(value = ContentSigningFormatResponseRequestDto.class, name = SigningWorkflowType.Codes.CONTENT_SIGNING),
})
@Schema(implementation = FormatResponseInterface.class)
public abstract class FormatResponseRequestDto implements FormatResponseInterface {

    private final SigningWorkflowType type;

    @Schema(
            description = "Data-to-be-signed bytes returned by formatDtbs (already contains serialNumber, signingTime, etc.)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] dtbs;

    @Schema(
            description = "Raw signature bytes from the Cryptography Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] signature;

    @Schema(
            description = "Certificate chain where the first element is the signer certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<byte[]> certificateChain;

    @Schema(
            description = "Formatter-specific parameters, same attributes as passed to formatDtbs",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> formatAttributes;

    protected FormatResponseRequestDto(SigningWorkflowType type) {
        this.type = type;
    }
}

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
        @Type(value = TimestampingFormatDtbsRequestDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
        @Type(value = ContentSigningFormatDtbsRequestDto.class, name = SigningWorkflowType.Codes.CONTENT_SIGNING),
})
@Schema(implementation = FormatDtbsInterface.class)
public abstract class FormatDtbsRequestDto implements FormatDtbsInterface {

    private final SigningWorkflowType type;

    @Schema(
            description = "Certificate chain where the first element is the signer certificate. Individual certificates must be Base64 DER encoded X.509 certificates.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<byte[]> certificateChain;

    @Schema(
            description = "Data to be formatted into the protocol-specific data-to-be-signed bytes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] data;

    @Schema(
            description = "Formatter-specific parameters (e.g. message imprint hash and algorithm, nonce, policy OID for TSA)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> formatAttributes;

    protected FormatDtbsRequestDto(SigningWorkflowType type) {
        this.type = type;
    }
}

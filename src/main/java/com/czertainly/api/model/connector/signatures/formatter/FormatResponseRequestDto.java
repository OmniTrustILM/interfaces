package com.czertainly.api.model.connector.signatures.formatter;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormatResponseRequestDto {

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

}

package com.czertainly.api.model.connector.signatures.signer;

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
public class ProxySignatureValidateRequestDto {

    @Schema(
            description = "Raw signature bytes or complete protocol container such as TimeStampToken or CAdES blob (proxy mode)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] signedObject;

    @Schema(
            description = "Original data or pre-computed hash.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] originalData;

    @Schema(
            description = "Connection parameters for the SignerInstance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> connectionAttributes;

    @Schema(
            description = "Operation-specific verify parameters (from POST /verify/attributes)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> verifyAttributes;

}

package com.otilm.api.model.connector.signatures.signer;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

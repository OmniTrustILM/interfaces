package com.otilm.api.model.connector.signatures.signer;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
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
public class ProxySignRequestDto {

    @Schema(
            description = "Data-to-be-signed bytes or raw protocol request bytes (proxy mode)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] data;

    @Schema(
            description = "Key identifier on the external service (e.g. key name for SignServer, credential ID for CSC API). " +
                    "Null when not overriding the pre-configured key on the external service.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String keyIdentifier;

    @Schema(
            description = "Signature algorithm used for signing. Null when not overriding the pre-configured algorithm on the external service.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private SignatureAlgorithm algorithm;

    @Schema(
            description = "Connection parameters for the SignerInstance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> connectionAttributes;

    @Schema(
            description = "Operation-specific sign parameters (from POST /sign/attributes)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> signAttributes;

}

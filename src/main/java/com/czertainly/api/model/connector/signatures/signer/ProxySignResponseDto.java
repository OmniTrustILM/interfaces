package com.czertainly.api.model.connector.signatures.signer;

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
public class ProxySignResponseDto {

    @Schema(
            description = "Raw signature bytes (component mode) or raw protocol response bytes (proxy mode), Base64-encoded",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] data;

    @Schema(
            description = "Certificate chain used for signing, ordered leaf-first, each entry DER-encoded and Base64-encoded. " +
                    "May be omitted if the certificate is already known to the caller.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<byte[]> certificateChain;

}

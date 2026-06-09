package com.otilm.api.model.connector.v3.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CrlResponseDto {

    @Schema(description = "Base64-encoded CRL (DER). Core parses nextUpdate, issuer, and revoked-entry data from the CRL itself.",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String crl;
}

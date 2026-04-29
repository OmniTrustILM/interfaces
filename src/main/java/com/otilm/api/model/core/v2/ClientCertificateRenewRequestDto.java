package com.otilm.api.model.core.v2;

import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing a request to renew certificate from external clients
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientCertificateRenewRequestDto {

    @Schema(
            description = "True to replace renewed certificate in the associated locations",
            defaultValue = "false"
    )
    private boolean replaceInLocations;

    @Schema(
            description = "Certificate signing request encoded as Base64 string. If not provided, Existing CSR will be used"
    )
    private String request;

    @Schema(
            description = "Certificate signing request format",
            defaultValue = "pkcs10"
    )
    @Builder.Default
    private CertificateRequestFormat format = CertificateRequestFormat.PKCS10;

}

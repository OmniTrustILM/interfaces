package com.otilm.api.model.core.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Class representing a request to sign CSR
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ClientCertificateRequestDto {

    @Schema(
            description = "RA Profile UUID. Required if CSR is not uploaded",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID raProfileUuid;

    @Schema(
            description = "Source certificate UUID to specify in case of renew/rekey operation"
    )
    private UUID sourceCertificateUuid;

    @Schema(
            description = "List of attributes to create CSR. Required if CSR is not provided"
    )
    List<RequestAttribute> csrAttributes;

    @Schema(
            description = "List of attributes to sign the CSR"
    )
    List<RequestAttribute> signatureAttributes;

    @Schema(
            description = "List of attributes to sign the alternative private key, in case of hybrid CSR"
    )
    List<RequestAttribute> altSignatureAttributes;

    //------------------------------------------------------------------------------------------------------------------
    // Key Related Parameters
    //------------------------------------------------------------------------------------------------------------------

    @Schema(
            description = "Certificate signing request encoded as Base64 string"
    )
    private String request;

    @Schema(
            description = "Certificate signing request format",
            defaultValue = "pkcs10"
    )
    @Builder.Default
    private CertificateRequestFormat format = CertificateRequestFormat.PKCS10;

    //------------------------------------------------------------------------------------------------------------------
    // Key Related Parameters
    //------------------------------------------------------------------------------------------------------------------

    @Schema(
            description = "Token Profile UUID. Required if CSR is not uploaded"
    )
    private UUID tokenProfileUuid;
    @Schema(
            description = "Key UUID. Required if CSR is not uploaded"
    )
    private UUID keyUuid;

    @Schema(
            description = "Token Profile UUID for the alternative key"
    )
    private UUID altTokenProfileUuid;
    @Schema(
            description = "Alternative Key UUID."
    )
    private UUID altKeyUuid;

    //------------------------------------------------------------------------------------------------------------------
    // Attributes
    //------------------------------------------------------------------------------------------------------------------

    @Schema(
            description = "List of RA Profile related Attributes to issue Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> issueAttributes;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;

}

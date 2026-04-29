package com.otilm.api.model.core.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

/**
 * Class representing a request to sign CSR from external clients
 */
@Data
public class ClientCertificateSignRequestDto {

    @Schema(
            description = "List of attributes to create CSR. Required if CSR is not provided"
    )
    List<RequestAttribute> csrAttributes;

    @Schema(
            description = "List of attributes to sign the CSR"
    )
    List<RequestAttribute> signatureAttributes;

    @Schema(
            description = "List of attributes to sign the alternative private key"
    )
    List<RequestAttribute> altSignatureAttributes;

    //------------------------------------------------------------------------------------------------------------------
    // Key Related Parameters
    //------------------------------------------------------------------------------------------------------------------

    @Schema(
            description = "Certificate signing request encoded as Base64 string",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String request;

    @Schema(
            description = "Certificate signing request format",
            defaultValue = "pkcs10"
    )
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
            description = "Token Profile UUID for the alternative key."
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
    private List<RequestAttribute> attributes;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .toString();
    }

}

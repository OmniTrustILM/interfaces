package com.otilm.api.model.core.location;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.core.certificate.CertificateState;
import com.otilm.api.model.core.certificate.CertificateValidationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CertificateInLocationDto {

    @Schema(
            description = "UUID of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String certificateUuid;

    @Schema(
            description = "State of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateState state;

    @Schema(
            description = "Current validation status of the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateValidationStatus validationStatus;

    @Schema(
            description = "Common Name of the Subject DN field of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String commonName;

    @Schema(
            description = "Serial number in HEX of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String serialNumber;

    @Schema(
            description = "Metadata of the Certificate in Location"
    )
    private List<MetadataResponseDto> metadata;

    @Schema(
            description = "Applied push attributes"
    )
    private List<ResponseAttribute> pushAttributes;

    @Schema(
            description = "Applied issue attributes"
    )
    private List<ResponseAttribute> csrAttributes;

    @Schema(
            description = "If the Certificate in Location has associated private key",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private boolean withKey;
}

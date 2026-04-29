package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CertificateContentDto {

    @Schema(
            description = "UUID of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String uuid;

    @Schema(
            description = "Certificate common name",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String commonName;

    @Schema(
            description = "Certificate serial number",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String serialNumber;

    @Schema(
            description = "Base64 encoded Certificate content",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String certificateContent;
}

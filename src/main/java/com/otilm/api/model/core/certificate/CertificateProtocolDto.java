package com.otilm.api.model.core.certificate;

import com.otilm.api.model.core.enums.CertificateProtocol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class CertificateProtocolDto {

    @Schema(description = "Protocol used to issue certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateProtocol protocol;

    @Schema(description = "UUID of the protocol", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID protocolProfileUuid;

    @Schema(description = "Additional UUID for use of the protocol, for example ACME Account UUID in case of ACME protocol")
    private UUID additionalProtocolUuid;

}

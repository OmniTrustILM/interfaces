package com.otilm.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class CertificateEventData implements EventData {


    @Schema(description = "Certificate UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID certificateUuid;

    @Schema(description = "SHA256 fingerprint of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    @Schema(description = "Certificate serial number", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "Subject DN of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer DN of the Certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;


}

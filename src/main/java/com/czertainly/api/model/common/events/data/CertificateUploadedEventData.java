package com.czertainly.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CertificateUploadedEventData extends CertificateEventData {

    @Schema(description = "UUID of the certificate user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID userUuid;

    @Override
    public String toString() {
        return "Certificate uploaded: {serialNumber='%s', subjectDn='%s', issuerDn='%s', userUuid=%s}"
                .formatted(getSerialNumber(), getSubjectDn(), getIssuerDn(), userUuid);
    }

}

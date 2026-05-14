package com.czertainly.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CertificateUploadedEventData extends CertificateEventData {

    @Schema(description = "UUID of the user to assigned the certificate user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID userUuid;

    @Override
    public String toString() {
        return "Certificate to be uploaded: {serialNumber='%s', subjectDn='%s', issuerDn='%s', userUuid=%s}"
                .formatted(getSerialNumber(), getSubjectDn(), getIssuerDn(), userUuid);
    }

}

package com.otilm.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
public class CertificateEventData implements EventData {

    @Schema(description = "Certificate UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID certificateUuid;

    @Schema(description = "SHA256 fingerprint of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    @Schema(description = "Name to show for the certificate: the common name of an X.509 certificate, the "
            + "identifier of a certificate type without one", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "Certificate serial number. Absent for certificate types without one, and for a "
            + "certificate that is registered and not yet issued", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "Subject DN of the Certificate. Absent for certificate types without a distinguished name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer DN of the Certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;

    @Override
    public String toString() {
        return "Certificate: {certificateUuid=%s, fingerprint='%s', displayName='%s', serialNumber='%s', subjectDn='%s', issuerDn='%s'}"
                .formatted(certificateUuid, fingerprint, displayName, serialNumber, subjectDn, issuerDn);
    }

}

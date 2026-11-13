package com.otilm.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
public class CertificateEventData implements EventData {

    @Schema(description = "Certificate UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID certificateUuid;

    @Schema(description = "SHA256 fingerprint of the Certificate. Absent for a certificate that is not issued yet "
            + "(registered, pending issuance, or failed)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Name to show for the certificate: the common name of an X.509 certificate, the "
            + "identifier of a certificate type without one", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "Certificate serial number. Absent for certificate types without one, and for a "
            + "certificate that is not issued yet (registered, pending issuance, or failed)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "Subject DN of the Certificate. Absent when the certificate has no subject distinguished "
            + "name: its type has none, or an X.509 certificate was registered with only alternative names.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer DN of the Certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder("Certificate: {certificateUuid=").append(certificateUuid);
        appendIfPresent(text, "fingerprint", fingerprint);
        appendIfPresent(text, "displayName", displayName);
        appendIfPresent(text, "serialNumber", serialNumber);
        appendIfPresent(text, "subjectDn", subjectDn);
        appendIfPresent(text, "issuerDn", issuerDn);
        return text.append('}').toString();
    }

    private static void appendIfPresent(StringBuilder text, String name, String value) {
        if (value != null) {
            text.append(", ").append(name).append("='").append(value).append('\'');
        }
    }

}

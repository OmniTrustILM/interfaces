package com.czertainly.api.model.common.events.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CertificateUploadedEventData extends CertificateEventData {

    private UUID userUuid;
    private List<String> customAttributes = new ArrayList<>();

    @Override
    public String toString() {
        return "Certificate to be uploaded: {serialNumber='%s', subjectDn='%s', issuerDn='%s', userUuid=%s, customAttributes=%s}"
                .formatted(getSerialNumber(), getSubjectDn(), getIssuerDn(), userUuid, customAttributes);
    }

}

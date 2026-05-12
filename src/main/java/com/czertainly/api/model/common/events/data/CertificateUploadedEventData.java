package com.czertainly.api.model.common.events.data;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import lombok.Data;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

@Data
public class CertificateUploadedEventData implements EventData {

    private X509Certificate certificate;
    private String fingerprint;
    private List<RequestAttribute> customAttributes;
    private UUID userUuid;

}

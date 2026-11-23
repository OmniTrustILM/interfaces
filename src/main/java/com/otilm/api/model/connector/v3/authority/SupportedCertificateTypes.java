package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.core.certificate.CertificateType;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Checks the certificate types a connector lists before they reach the caller. */
public final class SupportedCertificateTypes {

    private SupportedCertificateTypes() {
    }

    /**
     * @throws ConnectorException when an entry is missing, names no type, or repeats a type
     */
    public static List<SupportedCertificateTypeDtoV3> requireValid(List<SupportedCertificateTypeDtoV3> types,
            ApiClientConnectorInfo connector) throws ConnectorException {
        Set<CertificateType> seen = EnumSet.noneOf(CertificateType.class);
        for (SupportedCertificateTypeDtoV3 entry : types) {
            if (entry == null || entry.getCertificateType() == null) {
                throw new ConnectorException("Connector listed a supported certificate type without a type", connector);
            }
            if (!seen.add(entry.getCertificateType())) {
                throw new ConnectorException("Connector listed the certificate type "
                        + entry.getCertificateType().getCode() + " more than once", connector);
            }
        }
        return types;
    }
}

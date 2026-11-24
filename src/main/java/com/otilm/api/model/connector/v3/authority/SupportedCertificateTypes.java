package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.core.certificate.CertificateType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Reads the certificate types a connector lists before they reach the caller. */
public final class SupportedCertificateTypes {

    private SupportedCertificateTypes() {
    }

    /**
     * The listed types this version knows, in the order of the connector. A code it does not know is left out, since a
     * connector may be newer than its caller.
     *
     * @throws ConnectorException when an entry is missing, names no type, or repeats a type, or when types are listed
     * and none of them is known, which an empty result would report as X.509 only
     */
    public static List<SupportedCertificateTypeDtoV3> requireValid(List<ListedCertificateType> listed,
            ApiClientConnectorInfo connector) throws ConnectorException {
        Set<String> seen = new HashSet<>();
        List<SupportedCertificateTypeDtoV3> known = new ArrayList<>();
        for (ListedCertificateType entry : listed) {
            if (entry == null || entry.getCertificateType() == null) {
                throw new ConnectorException("Connector listed a supported certificate type without a type", connector);
            }
            String code = entry.getCertificateType();
            if (!seen.add(code)) {
                throw new ConnectorException("Connector listed the certificate type " + code + " more than once",
                        connector);
            }
            knownType(code).map(SupportedCertificateTypeDtoV3::of).ifPresent(known::add);
        }
        if (!listed.isEmpty() && known.isEmpty()) {
            throw new ConnectorException("Connector listed only certificate types this version does not know",
                    connector);
        }
        return known;
    }

    private static Optional<CertificateType> knownType(String code) {
        return Arrays.stream(CertificateType.values()).filter(type -> type.getCode().equals(code)).findFirst();
    }
}

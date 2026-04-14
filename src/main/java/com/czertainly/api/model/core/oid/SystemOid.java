package com.czertainly.api.model.core.oid;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SystemOid {

    // RDN Attribute Type
    EMAIL("1.2.840.113549.1.9.1", "Email", OidCategory.RDN_ATTRIBUTE_TYPE, "EMAIL", List.of("E", "EMAILADDRESS")),
    COMMON_NAME("2.5.4.3", "Common Name", OidCategory.RDN_ATTRIBUTE_TYPE, "CN", List.of()),
    ORGANIZATION_UNIT("2.5.4.11", "Organization Unit", OidCategory.RDN_ATTRIBUTE_TYPE, "OU", List.of()),
    ORGANIZATION("2.5.4.10", "Organization", OidCategory.RDN_ATTRIBUTE_TYPE, "O", List.of()),
    LOCALITY("2.5.4.7", "Locality", OidCategory.RDN_ATTRIBUTE_TYPE, "L", List.of()),
    STATE("2.5.4.8", "State", OidCategory.RDN_ATTRIBUTE_TYPE, "ST", List.of("S")),
    DOMAIN_COMPONENT("0.9.2342.19200300.100.1.25", "Domain Component", OidCategory.RDN_ATTRIBUTE_TYPE, "DC", List.of()),
    COUNTRY("2.5.4.6", "Country", OidCategory.RDN_ATTRIBUTE_TYPE, "C", List.of()),
    DISTINGUISHED_NAME_QUALIFIER("2.5.4.46", "Distinguished Name Qualifier", OidCategory.RDN_ATTRIBUTE_TYPE, "DNQ", List.of()),
    TITLE("2.5.4.12", "Title", OidCategory.RDN_ATTRIBUTE_TYPE, "T", List.of("TITLE")),
    SURNAME("2.5.4.4", "Surname", OidCategory.RDN_ATTRIBUTE_TYPE, "SN", List.of("SURNAME")),
    GIVEN_NAME("2.5.4.42", "Given Name", OidCategory.RDN_ATTRIBUTE_TYPE, "GIVENNAME", List.of()),
    INITIALS("2.5.4.43", "Initials", OidCategory.RDN_ATTRIBUTE_TYPE, "INITIALS", List.of()),
    PSEUDONYM("2.5.4.65", "Pseudonym", OidCategory.RDN_ATTRIBUTE_TYPE, "PSEUDONYM", List.of()),
    GENERATION_QUALIFIER("2.5.4.44", "Generation Qualifier", OidCategory.RDN_ATTRIBUTE_TYPE, "GENERATION", List.of()),

    // Extended Key Usage
    SERVER_AUTH("1.3.6.1.5.5.7.3.1", "TLS Web Server Authentication", OidCategory.EXTENDED_KEY_USAGE),
    CLIENT_AUTH("1.3.6.1.5.5.7.3.2", "TLS Web Client Authentication", OidCategory.EXTENDED_KEY_USAGE),
    CODE_SIGNING("1.3.6.1.5.5.7.3.3", "Code Signing", OidCategory.EXTENDED_KEY_USAGE),
    EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4", "Email Protection", OidCategory.EXTENDED_KEY_USAGE),
    TIME_STAMPING("1.3.6.1.5.5.7.3.8", "Time Stamping", OidCategory.EXTENDED_KEY_USAGE),
    OCSP_SIGNING("1.3.6.1.5.5.7.3.9", "OCSP Signing", OidCategory.EXTENDED_KEY_USAGE),
    DOCUMENT_SIGNING("1.3.6.1.4.1.311.10.3.12","Document Signing", OidCategory.EXTENDED_KEY_USAGE),
    IPSEC_USER("1.3.6.1.5.5.8.2.2", "IPSec User", OidCategory.EXTENDED_KEY_USAGE),
    IPSEC_END_SYSTEM("1.3.6.1.5.5.7.3.5", "IPSec End System", OidCategory.EXTENDED_KEY_USAGE),
    IPSEC_TUNNEL("1.3.6.1.5.5.7.3.6", "IPSec Tunnel", OidCategory.EXTENDED_KEY_USAGE),
    AUTHENTIC_DOCUMENTS_TRUST("1.2.840.113583.1.1.5", "Authentic Documents Trust", OidCategory.EXTENDED_KEY_USAGE),

    // QC Statements for time-stamp tokens (ETSI EN 319 422 Annex B)
    QTST_STATEMENT_1("0.4.0.19422.1.1", "Qualified Electronic Time-Stamp", OidCategory.QC_STATEMENT)
    ;

    private static final SystemOid[] VALUES;

    static {
        VALUES = values();
    }

    private final String oid;
    private final String displayName;
    private final OidCategory category;
    private final String code;
    private final List<String> altCodes;

    SystemOid(String oid, String displayName, OidCategory category, String code, List<String> altCodes) {
        this.oid = oid;
        this.displayName = displayName;
        this.category = category;
        this.code = code;
        this.altCodes = altCodes;
    }

    SystemOid(String oid, String displayName, OidCategory category) {
        this(oid, displayName, category, null, null);
    }

    public static SystemOid fromOID(String oid) {
        return Arrays.stream(VALUES)
                .filter(e -> e.oid.equals(oid))
                .findFirst()
                .orElse(null);
    }

}

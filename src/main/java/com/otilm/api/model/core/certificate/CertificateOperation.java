package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * An operation a certificate can support, whatever its type. Not to be confused with
 * {@link com.otilm.api.model.core.v2.CertificateOperationKind}, which says what an authority advertises: whether an
 * operation is available for a certificate combines what its type supports with what its authority supports.
 */
@Schema(enumAsRef = true)
public enum CertificateOperation implements IPlatformEnum {

    REVOKE("revoke", "Revoke", "Revoke the certificate"),
    RENEW("renew", "Renew", "Renew the certificate with the same key"),
    REKEY("rekey", "Rekey", "Renew the certificate with a new key"),
    VALIDATE("validate", "Validate", "Validate the certificate"),
    CHECK_COMPLIANCE("checkCompliance", "Check compliance", "Check the certificate against compliance profiles"),
    VALIDATE_CHAIN("validateChain", "Validate chain", "Build and validate the chain of the certificate"),
    PUSH_TO_LOCATION("pushToLocation", "Push to location", "Push the certificate to a location"),
    DISCOVER("discover", "Discover", "Discover certificates of the type"),
    ENROLL_ACME("enrollAcme", "Enroll with ACME", "Enrollment through the ACME protocol"),
    ENROLL_SCEP("enrollScep", "Enroll with SCEP", "Enrollment through the SCEP protocol"),
    ENROLL_CMP("enrollCmp", "Enroll with CMP", "Enrollment through the CMP protocol"),
    ENROLL_EST("enrollEst", "Enroll with EST", "Enrollment through the EST protocol");

    private static final CertificateOperation[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CertificateOperation(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static CertificateOperation fromCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported certificate operation %s.", code)));
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

package com.otilm.api.model.core.authority;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.security.cert.CRLReason;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CertificateRevocationReason implements IPlatformEnum {

    UNSPECIFIED("unspecified", "Unspecified", 0, CRLReason.UNSPECIFIED),
    KEY_COMPROMISE("keyCompromise", "Key compromise", 1, CRLReason.KEY_COMPROMISE),
    CA_COMPROMISE("cACompromise", "CA compromise", 2, CRLReason.CA_COMPROMISE),
    AFFILIATION_CHANGED("affiliationChanged", "Affiliation changed", 3, CRLReason.AFFILIATION_CHANGED),
    SUPERSEDED("superseded", "Superseded", 4, CRLReason.SUPERSEDED),
    CESSATION_OF_OPERATION("cessationOfOperation", "Cessation of operation", 5, CRLReason.CESSATION_OF_OPERATION),
    CERTIFICATE_HOLD("certificateHold", "Certificate hold", 6, CRLReason.CERTIFICATE_HOLD),
    PRIVILEGES_WITHDRAWN("privilegeWithdrawn", "Privilege withdrawn", 9, CRLReason.PRIVILEGE_WITHDRAWN),
    AA_COMPROMISE("aACompromise", "AA compromise", 10, CRLReason.CA_COMPROMISE);

    private static final CertificateRevocationReason[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Revocation Reason code",
            examples = {"keyCompromise"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final int reasonCode;
    private final CRLReason crlReason;

    CertificateRevocationReason(String code, String label, int reasonCode, CRLReason crlReason) {
        this.code = code;
        this.label = label;
        this.reasonCode = reasonCode;
        this.crlReason = crlReason;
    }

    @JsonCreator
    public static CertificateRevocationReason fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(r -> r.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Certificate Revocation reason {}", code)));
    }

    public static CertificateRevocationReason fromReasonCode(int reasonCode) {
        return Arrays.stream(VALUES)
                .filter(r -> r.reasonCode == reasonCode)
                .findFirst()
                .orElse(null);
    }

    public static CertificateRevocationReason fromCrlReason(CRLReason crlReason) {
        return Arrays.stream(VALUES)
                .filter(r -> r.crlReason == crlReason)
                .findFirst()
                .orElse(null);
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public int getReasonCode() {
        return reasonCode;
    }
}

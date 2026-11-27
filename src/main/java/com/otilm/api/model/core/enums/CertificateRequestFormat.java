package com.otilm.api.model.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true,
        description = "Format of a certificate request. The request is always sent Base64-encoded. pkcs10 and crmf "
                + "are the DER of the PKCS#10 or CRMF structure and apply to X.509. cvc is the DER of the complete "
                + "card verifiable certificate request, including the authentication data object around it when the "
                + "request is authenticated, and applies to CVC")
public enum CertificateRequestFormat implements IPlatformEnum {

    PKCS10("pkcs10", "PKCS#10", "PKCS#10 Certificate Request"),
    CRMF("crmf", "CRMF", "Certificate Request Message Format"),
    CVC("cvc", "CVC", "Card verifiable certificate request, BSI TR-03110 Part 3");

    private final String code;
    private final String label;
    private final String description;

    CertificateRequestFormat(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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
        return this.description;
    }

    @JsonCreator
    public static CertificateRequestFormat fromCode(final String code) {
        return Arrays
                .stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported certificate request format %s.", code)));
    }
}

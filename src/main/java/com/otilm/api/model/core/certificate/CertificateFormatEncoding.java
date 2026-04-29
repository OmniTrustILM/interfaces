package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CertificateFormatEncoding implements IPlatformEnum {

    PEM("pem", "PEM", "PEM certificate format encoding"),
    DER("der", "DER", "DER certificate format encoding");


    private final String code;
    private final String label;
    private final String description;

    private static final CertificateFormatEncoding[] VALUES;

    static {
        VALUES = values();
    }

    CertificateFormatEncoding(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static CertificateFormatEncoding fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported certificate format encoding %s.", code)));
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
}

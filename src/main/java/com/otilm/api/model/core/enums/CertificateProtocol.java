package com.otilm.api.model.core.enums;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum CertificateProtocol implements IPlatformEnum {

    ACME("acme", "ACME Protocol"),
    SCEP("scep", "SCEP Protocol"),
    CMP("cmp", "CMP Protocol")
    ;

    private final String code;
    private final String label;

   CertificateProtocol(String code, String label) {
        this.code = code;
        this.label = label;
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
}

package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum CertificateSubjectType implements IPlatformEnum {

    END_ENTITY("endEntity", "End Entity", "Certificate issued by a Certificate Authority."),
    SELF_SIGNED_END_ENTITY("selfSignedEndEntity", "Self-signed End Entity", "Certificate signed by itself, not issued by certificate authority."),
    INTERMEDIATE_CA("intermediateCa", "Intermediate CA", "Certificate of certificate authority that is used to issue end entity certificate and isn’t the top of the chain authority."),
    ROOT_CA("rootCa", "Root CA", "Certificate of the top of the chain certificate authority.");

    private final String code;
    private final String label;
    private final String description;

    CertificateSubjectType(String code, String label, String description) {
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

    public boolean isCa() {
        return this.equals(ROOT_CA) || this.equals(INTERMEDIATE_CA);
    }

    public boolean isSelfSigned() {
        return this.equals(ROOT_CA) || this.equals(SELF_SIGNED_END_ENTITY);
    }
}

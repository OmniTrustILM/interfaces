package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ObjectType implements IPlatformEnum {

    X509_CERTIFICATE("x509Certificate", "X.509 Certificate"),
    SSH_CERTIFICATE("sshCertificate", "SSH Certificate"),
    KEY("key", "Key"),
    SECRET("secret", "Secret");

    private static final ObjectType[] VALUES = values();

    private final String code;
    private final String label;

    ObjectType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    @JsonValue
    public String getCode() { return code; }

    @Override
    public String getLabel() { return label; }

    @Override
    public String getDescription() { return null; }

    public static ObjectType findByCode(String code) {
        return Arrays.stream(VALUES).filter(v -> v.code.equals(code)).findFirst().orElse(null);
    }

    @JsonCreator
    public static ObjectType fromCode(String code) { return findByCode(code); }
}

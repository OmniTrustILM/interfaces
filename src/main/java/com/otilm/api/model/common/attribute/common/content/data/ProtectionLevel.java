package com.otilm.api.model.common.attribute.common.content.data;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ProtectionLevel implements IPlatformEnum {
    NONE("none", "No Protection", "Attribute content is in plain text"),
    ENCRYPTED("encrypted", "Encrypted", "Attribute content is encrypted by a PBE cipher")
    ;

    private final String code;
    private final String label;
    private final String description;

    ProtectionLevel(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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

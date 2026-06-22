package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum FieldSource implements IPlatformEnum {

    CSR("csr", "CSR"),
    PLATFORM("platform", "Platform"),
    CSR_THEN_PLATFORM("csrThenPlatform", "CSR, falling back to Platform");

    private static final FieldSource[] VALUES = values();

    private final String code;
    private final String label;

    FieldSource(String code, String label) {
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

    public static FieldSource findByCode(String code) {
        return Arrays.stream(VALUES).filter(v -> v.code.equals(code)).findFirst().orElse(null);
    }

    @JsonCreator
    public static FieldSource fromCode(String code) { return findByCode(code); }
}

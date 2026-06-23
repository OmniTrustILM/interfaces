package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ValueSourceType implements IPlatformEnum {

    NONE("none", "None (free input)"),
    CONNECTOR_CALLBACK("connectorCallback", "Connector Callback"),
    STATIC_LIST("staticList", "Static List");

    private static final ValueSourceType[] VALUES = values();

    private final String code;
    private final String label;

    ValueSourceType(String code, String label) {
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

    @JsonCreator
    public static ValueSourceType fromCode(String code) {
        return Arrays.stream(VALUES).filter(v -> v.code.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ValueSourceType code: " + code));
    }
}

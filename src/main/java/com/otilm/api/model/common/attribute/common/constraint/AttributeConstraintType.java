package com.otilm.api.model.common.attribute.common.constraint;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum AttributeConstraintType implements IPlatformEnum {
    REGEXP(Codes.REGEXP, "Regular Expression"),
    RANGE(Codes.RANGE, "Integer Range"),
    DATETIME(Codes.DATETIME, "DateTime Range");

    private static final AttributeConstraintType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    AttributeConstraintType(String code, String label) {
        this(code, label,null);
    }

    AttributeConstraintType(String code, String label, String description) {
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
    public static AttributeConstraintType fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported attribute constraint type %s.", code)));
    }

    public static class Codes {

        private Codes() {

        }

        public static final String REGEXP = "regExp";
        public static final String RANGE = "range";
        public static final String DATETIME = "dateTime";

    }

}

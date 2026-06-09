package com.otilm.api.model.common.attribute.common.callback;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum AttributeValueTarget implements IPlatformEnum {

    PATH_VARIABLE("pathVariable", "Path variable"),
    REQUEST_PARAMETER("requestParameter", "Request parameter"),
    BODY("body", "Body property"),
    FILTER("filter", "Filter for resource content type");

    private static final AttributeValueTarget[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;

    private final String label;

    private final String description;

    AttributeValueTarget(String code, String label) {
        this(code, label,null);
    }

    AttributeValueTarget(String code, String label, String description) {
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
    public static AttributeValueTarget findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Attribute Callback Value Target {}", code)));
    }
}

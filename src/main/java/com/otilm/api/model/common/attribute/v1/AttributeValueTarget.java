package com.otilm.api.model.common.attribute.v1;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

public enum AttributeValueTarget {

    PATH_VARIABLE("pathVariable"),
    REQUEST_PARAMETER("requestParameter"),
    BODY("body");
    @Schema(description = "Attribute value Target",
            examples = {"pathVariable"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    AttributeValueTarget(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static AttributeValueTarget findByCode(String code) {
        return Arrays.stream(AttributeValueTarget.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Target {}", code)));
    }
}

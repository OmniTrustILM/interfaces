package com.otilm.api.model.core.search;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum FilterFieldType implements IPlatformEnum {

    STRING(Constants.STRING, "String"),
    NUMBER(Constants.NUMBER, "Number"),
    LIST(Constants.LIST, "List"),
    DATE(Constants.DATE, "Date"),
    DATETIME(Constants.DATETIME, "DateTime"),
    BOOLEAN(Constants.BOOLEAN, "Boolean");

    private static final FilterFieldType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    FilterFieldType(String code, String label) {
        this(code, label,null);
    }

    FilterFieldType(String code, String label, String description) {
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
    public static FilterFieldType fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported search field type %s.", code)));
    }

    private static class Constants {
        /**
         * Simple text attribute
         **/
        private static final String STRING = "string";

        /**
         * Simple number attribute
         **/
        private static final String NUMBER = "number";

        /**
         * Attribute with predefined value with list of elements
         **/
        private static final String LIST = "list";

        /**
         * Special attribute type representing credential
         **/
        private static final String DATE = "date";

        /**
         * Special attribute type representing credential
         **/
        private static final String DATETIME = "datetime";

        /**
         * Value to compare the boolean values
         **/
        private static final String BOOLEAN = "boolean";
    }
}

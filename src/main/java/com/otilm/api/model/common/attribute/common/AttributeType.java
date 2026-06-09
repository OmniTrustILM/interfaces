package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * This class defines Attribute types.
 */
@Schema(enumAsRef = true)
public enum AttributeType implements IPlatformEnum {

	DATA(Codes.DATA, "Data"),
    GROUP(Codes.GROUP, "Group"),
    INFO(Codes.INFO, "Info"),
    META(Codes.META, "Metadata"),
    CUSTOM(Codes.CUSTOM, "Custom");

    private static final AttributeType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;

    private final String label;

    private final String description;

    AttributeType(String code, String label) {
        this(code, label,null);
    }

    AttributeType(String code, String label, String description) {
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
    public static AttributeType fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported attribute type %s.", code)));
    }
    
    public static class Codes {
    	/** Data Attributes **/
        public static final String DATA = "data";

        /** Group Attributes **/
        public static final String GROUP = "group";

        /** Info Attributes **/
        public static final String INFO = "info";

        /** Custom Attributes **/
        public static final String CUSTOM = "custom";

        /** Meta Data Attributes **/
        public static final String META = "meta";

        private Codes() {

        }
    }
}

package com.otilm.api.model.common.attribute.common.content;

import com.otilm.api.model.common.attribute.common.content.data.CodeBlockAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.otilm.api.model.common.attribute.v2.content.*;
import com.otilm.api.model.common.attribute.v3.content.*;
import com.otilm.api.model.common.attribute.v3.content.data.ResourceObjectContentData;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * This class defines Attribute Content types.
 */
@Schema(enumAsRef = true)
public enum AttributeContentType implements IPlatformEnum {

    STRING(Codes.STRING, "String", StringAttributeContentV2.class, StringAttributeContentV3.class, String.class, true),
    TEXT(Codes.TEXT, "Text", TextAttributeContentV2.class, TextAttributeContentV3.class, String.class, true),
    INTEGER(Codes.INTEGER, "Integer number", IntegerAttributeContentV2.class, IntegerAttributeContentV3.class, Integer.class, true),
    BOOLEAN(Codes.BOOLEAN, "Boolean", BooleanAttributeContentV2.class, BooleanAttributeContentV3.class, Boolean.class, true),
    FLOAT(Codes.FLOAT, "Decimal number", FloatAttributeContentV2.class, FloatAttributeContentV3.class, Float.class, true),
    DATE(Codes.DATE, "Date", DateAttributeContentV2.class, DateAttributeContentV3.class, LocalDate.class, true),
    TIME(Codes.TIME, "Time", TimeAttributeContentV2.class, TimeAttributeContentV3.class, LocalTime.class, true),
    DATETIME(Codes.DATETIME, "DateTime", DateTimeAttributeContentV2.class, DateTimeAttributeContentV3.class, ZonedDateTime.class, true),
    SECRET(Codes.SECRET, "Secret", SecretAttributeContentV2.class, null, SecretAttributeContentData.class, false),
    FILE(Codes.FILE, "File", FileAttributeContentV2.class, FileAttributeContentV3.class, FileAttributeContentData.class, false),
    CREDENTIAL(Codes.CREDENTIAL, "Credential", CredentialAttributeContentV2.class, null, CredentialAttributeContentData.class, false),
    CODEBLOCK(Codes.CODEBLOCK, "Code block", CodeBlockAttributeContentV2.class, CodeBlockAttributeContentV3.class, CodeBlockAttributeContentData.class, false),
    OBJECT(Codes.OBJECT, "Object", ObjectAttributeContentV2.class, ObjectAttributeContentV3.class, Object.class, false),
    RESOURCE(Codes.RESOURCE, "Resource Object", null, ResourceObjectContent.class, ResourceObjectContentData.class, false);


    private static final AttributeContentType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    @Getter
    private final Class<?> contentV2Class;

    @Getter
    private final Class<?> contentV3Class;
    @Getter
    private final Class<?> contentDataClass;

    @Getter
    private final boolean filterByData;


    AttributeContentType(String code, String label, Class<?> contentV2Class, Class<?> contentV3Class, Class<?> dataJavaClass, boolean filterByData) {
        this(code, label, null, contentV2Class, contentV3Class, dataJavaClass, filterByData);
    }

    AttributeContentType(String code, String label, String description, Class<?> contentV2Class,
                         Class<?> contentV3Class, Class<?> contentDataClass, boolean filterByData) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.contentV2Class = contentV2Class;
        this.contentV3Class = contentV3Class;
        this.contentDataClass = contentDataClass;
        this.filterByData = filterByData;
    }

    @JsonCreator
    public static AttributeContentType fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported attribute content type %s.", code)));
    }

    public static AttributeContentType fromClass(Class<?> clazz) {
        return clazz.equals(BaseAttributeContentV2.class) ? null
                : Arrays.stream(VALUES).filter(e -> e.contentV2Class.equals(clazz))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported attribute content type for class %s.", clazz)));
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

    public static class Codes {


        private Codes() {
        }

        /**
         * Simple text Attribute
         **/
        public static final String STRING = "string";

        /**
         * Simple integer Attribute
         **/
        public static final String INTEGER = "integer";

        /**
         * String Attribute containing sensitive data
         **/
        public static final String SECRET = "secret";

        /**
         * Attribute containing file data in Base64 string
         **/
        public static final String FILE = "file";

        /**
         * Boolean Attribute taking on values {@code true} or {@code false}
         **/
        public static final String BOOLEAN = "boolean";

        /**
         * Special Attribute type representing Credential
         **/
        public static final String CREDENTIAL = "credential";

        /**
         * Attribute type representing date only
         **/
        public static final String DATE = "date";

        /**
         * Attribute type representing float number
         **/
        public static final String FLOAT = "float";

        /**
         * Attribute type representing general data object
         **/
        public static final String OBJECT = "object";

        /**
         * Attribute type representing text, which is multiline data
         **/
        public static final String TEXT = "text";

        /**
         * Attribute type representing time only
         **/
        public static final String TIME = "time";

        /**
         * Attribute type representing date and time
         **/
        public static final String DATETIME = "datetime";

        /**
         * Attribute type representing code block
         **/
        public static final String CODEBLOCK = "codeblock";

        /**
         * Attribute type representing resource object
         **/
        public static final String RESOURCE = "resource";
    }
    
}

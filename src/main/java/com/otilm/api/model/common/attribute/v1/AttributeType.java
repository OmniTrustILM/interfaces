package com.otilm.api.model.common.attribute.v1;

import java.util.Arrays;

/**
 * This class defines Attribute data types.
 */
public enum AttributeType {
	
	STRING(Constants.STRING),
    INTEGER(Constants.INTEGER),
	SECRET(Constants.SECRET),
	FILE(Constants.FILE),
	BOOLEAN(Constants.BOOLEAN),
    CREDENTIAL(Constants.CREDENTIAL),
    DATE(Constants.DATE),
    FLOAT(Constants.FLOAT),
    JSON(Constants.JSON),
    TEXT(Constants.TEXT),
    TIME(Constants.TIME),
    DATETIME(Constants.DATETIME),
    CODEBLOCK(Constants.CODEBLOCK);

    private final String code;

    AttributeType(String string) {
        this.code = string;
    }

    public String getCode() {
        return code;
    }

    public static AttributeType fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }

    public static Class getClass(AttributeType code) {
        switch (code) {
            case STRING:
            case SECRET:
            case TEXT:
                return String.class;
            case INTEGER:
                return Integer.class;
            case BOOLEAN:
                return Boolean.class;
            case FLOAT:
                return Float.class;
            default:
                return null;
        }
    }
    
    private static class Constants {
    	/** Simple text Attribute **/
        private static final String STRING = "string";

        /** Simple integer Attribute **/
        private static final String INTEGER = "integer";

        /** String Attribute containing sensitive data **/
        private static final String SECRET = "secret";

        /** Attribute containing file data in Base64 string **/
        private static final String FILE = "file";

        /** Boolean Attribute taking on values {@code true} or {@code false} **/
        private static final String BOOLEAN = "boolean";

        /** Special Attribute type representing Credential **/
        private static final String CREDENTIAL = "credential";

        /** Attribute type representing date only **/
        private static final String DATE = "date";

        /** Attribute type representing float number **/
        private static final String FLOAT = "float";

        /** Attribute type representing general JSON data object **/
        private static final String JSON = "json";

        /** Attribute type representing text, which is multiline data **/
        private static final String TEXT = "text";

        /** Attribute type representing time only **/
        private static final String TIME = "time";

        /** Attribute type representing date and time **/
        private static final String DATETIME = "datetime";

        /** Attribute type representing code block **/
        private static final String CODEBLOCK = "codeblock";
    }
}

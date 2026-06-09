package com.otilm.api.model.connector.secrets;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SecretType implements IPlatformEnum {
    BASIC_AUTH(Codes.BASIC_AUTH, "Basic Authentication"),
    API_KEY(Codes.API_KEY, "API Key"),
    JWT_TOKEN(Codes.JWT_TOKEN, "JWT Token"),
    PRIVATE_KEY(Codes.PRIVATE_KEY, "Private Key"),
    SECRET_KEY(Codes.SECRET_KEY, "Secret Key"),
    KEY_STORE(Codes.KEY_STORE, "Key Store"),
    KEY_VALUE(Codes.KEY_VALUE, "Key-Value Pairs"),
    GENERIC(Codes.GENERIC, "Generic");

    private static final SecretType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SecretType(String code, String label) {
        this(code, label, null);
    }

    SecretType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SecretType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown secret type {}", code)));
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

        public static final String BASIC_AUTH = "basicAuth";
        public static final String API_KEY = "apiKey";
        public static final String JWT_TOKEN = "jwtToken";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String SECRET_KEY = "secretKey";
        public static final String KEY_STORE = "keyStore";
        public static final String KEY_VALUE = "keyValue";
        public static final String GENERIC = "generic";

        private Codes() {

        }
    }
}

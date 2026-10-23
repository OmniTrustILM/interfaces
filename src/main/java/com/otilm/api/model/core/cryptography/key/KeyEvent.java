package com.otilm.api.model.core.cryptography.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

public enum KeyEvent {
    CREATE("Create Key"),
    IMPORT("Import Key"),
    ONBOARD("Onboard Key"),
    EXPORT("Export Key"),
    EXPORT_DISABLED("Disable Key Export"),
    COMPROMISED("Compromised Key"),
    DESTROY("Destroy Key"),
    UPDATE_USAGE("Update Key Usages"),
    SIGN("Sign Data"),
    VERIFY("Verify Data"),
    ENCRYPT("Encrypt Data"),
    DECRYPT("Decrypt Data"),
    ENABLE("Enable Key"),
    DISABLE("Disable Key");

    @Schema(description = "Key Event", examples = {"Create Key"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    KeyEvent(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static KeyEvent findByCode(String code) {
        return Arrays
                .stream(KeyEvent.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown event {}", code)));
    }
}

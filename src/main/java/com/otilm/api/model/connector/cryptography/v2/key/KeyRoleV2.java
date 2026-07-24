package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Key roles supported by the v2 connector contract.
 */
@Schema(name = "KeyRoleV2", description = "Key role supported by the v2 connector contract", enumAsRef = true)
public enum KeyRoleV2 {
    SECRET("Secret"),
    PUBLIC("Public"),
    PRIVATE("Private");

    private final String code;

    KeyRoleV2(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static KeyRoleV2 findByCode(String code) {
        return Arrays.stream(values())
                .filter(role -> role.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown v2 key role: " + code));
    }
}

package com.otilm.api.model.core.cryptography.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true, description = "Onboarding status of a key found in the key storage")
public enum TokenKeyStatus implements IPlatformEnum {
    ONBOARDABLE("onboardable", "Onboardable", "The key can be onboarded into the inventory"),
    ONBOARDABLE_INTO_KEY("onboardableIntoKey", "Onboardable into an existing key",
            "The platform inventory holds the public key without a token instance. Onboarding adds the private key to that key."),
    IN_INVENTORY("inInventory", "In inventory", "The platform inventory already holds this key"),
    IN_INVENTORY_WITHOUT_TOKEN("inInventoryWithoutToken", "In inventory without token instance",
            "The platform inventory holds the public key without a token instance, in a key that cannot take this private key."),
    IMPORT_IN_PROGRESS("importInProgress", "Import in progress",
            "An import of this key is still in progress. Onboarding waits until it settles."),
    NO_PUBLIC_KEY("noPublicKey", "No public key in key storage",
            "The key storage lacks a public key object for this private key. Onboarding requires one."),
    NO_UNIQUE_ID("noUniqueId", "No unique ID in key storage",
            "The key storage holds this key without an ID unique to it. Onboarding requires one.");

    private static final TokenKeyStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TokenKeyStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static TokenKeyStatus findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown token key status {}", code)));
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
}

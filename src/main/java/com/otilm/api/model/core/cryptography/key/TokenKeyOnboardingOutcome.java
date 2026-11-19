package com.otilm.api.model.core.cryptography.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true, description = "What became of one key selected for onboarding")
public enum TokenKeyOnboardingOutcome implements IPlatformEnum {

    ONBOARDED("onboarded", "Onboarded", "The key was onboarded as a new inventory key"),
    JOINED_EXISTING_KEY("joinedExistingKey", "Joined an existing key",
            "The private key joined the inventory key that held its public key"),
    NAME_TAKEN("nameTaken", "Name taken", "Another inventory key has the name, so the key was skipped"),
    BLOCKED("blocked", "Blocked",
            "The key's state in the key storage or in the inventory stops onboarding until that state changes"),
    FAILED("failed", "Failed",
            "Onboarding the key failed unexpectedly or raced with a change, and a retry may succeed");

    private static final TokenKeyOnboardingOutcome[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TokenKeyOnboardingOutcome(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static TokenKeyOnboardingOutcome findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(outcome -> outcome.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown token key onboarding outcome {}", code)));
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

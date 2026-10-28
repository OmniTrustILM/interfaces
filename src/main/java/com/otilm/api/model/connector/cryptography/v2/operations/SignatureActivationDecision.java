package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import java.util.Arrays;
import java.util.Optional;

/** Who authorized a signature, as the platform states it in the {@link SignatureActivationAttribute}. */
public enum SignatureActivationDecision implements IPlatformEnum {

    PLATFORM_ASSERTED("platformAsserted", "Platform asserted",
            "The platform authorized the signature for the signer bound to the key");

    private final String code;
    private final String label;
    private final String description;

    SignatureActivationDecision(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public static Optional<SignatureActivationDecision> lookupByCode(String code) {
        return Arrays.stream(values()).filter(decision -> decision.code.equals(code)).findFirst();
    }

    @JsonCreator
    public static SignatureActivationDecision findByCode(String code) {
        return lookupByCode(code)
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown signature activation decision {}", code)));
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

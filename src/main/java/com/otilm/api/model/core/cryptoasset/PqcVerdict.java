package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * Post-quantum readiness verdict of a cryptographic asset, computed by the platform rule set. The rule that produced a
 * verdict travels with the asset detail, so a verdict is always attributable to the rule that made it.
 */
@Schema(enumAsRef = true, description = "Post-quantum readiness verdict computed by the platform rule set")
public enum PqcVerdict implements IPlatformEnum {

    READY(Codes.READY, "PQC ready", "The asset withstands a cryptographically relevant quantum computer"),
    NOT_READY(Codes.NOT_READY, "Not PQC ready", "The asset relies on cryptography a quantum computer breaks"),
    NOT_APPLICABLE(Codes.NOT_APPLICABLE, "Not applicable", "Post-quantum migration does not apply to the asset"),
    UNKNOWN(Codes.UNKNOWN, "Unknown", "The rule set cannot classify the asset from the recorded properties");

    public static class Codes {
        public static final String READY = "ready";
        public static final String NOT_READY = "notReady";
        public static final String NOT_APPLICABLE = "notApplicable";
        public static final String UNKNOWN = "unknown";

        private Codes() {
        }
    }

    private static final PqcVerdict[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    PqcVerdict(String code, String label, String description) {
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
    public static PqcVerdict findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown PQC verdict {}", code)));
    }
}

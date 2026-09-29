package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * What one rule of the PQC rule set did while a verdict was explained. Evaluation is first-match-wins, so exactly one
 * step of an explanation is {@link #DECIDED} or {@link #RESOLVED}; the steps before it are {@link #NOT_MATCHED} and the
 * steps after it {@link #NOT_REACHED}. An asset the rule set cannot evaluate is explained by a single {@link #FAILED}
 * step instead.
 */
@Schema(enumAsRef = true, description = "What one rule of the PQC rule set did while the verdict was explained")
public enum PqcExplanationStepOutcome implements IPlatformEnum {

    NOT_MATCHED(Codes.NOT_MATCHED, "Not matched", "The rule's condition did not hold for the asset"),
    DECIDED(Codes.DECIDED, "Decided", "The rule's condition held and its verdict is the asset's"),
    NOT_REACHED(Codes.NOT_REACHED, "Not reached", "An earlier rule decided, so this rule was not evaluated"),
    RESOLVED(Codes.RESOLVED, "Resolved",
            "The rule's condition held and the verdict is the one stored on another inventory asset the asset refers "
                    + "to"),
    FAILED(Codes.FAILED, "Failed",
            "The rule set could not be evaluated against the asset's recorded properties, so no rule decided");

    public static class Codes {
        public static final String NOT_MATCHED = "notMatched";
        public static final String DECIDED = "decided";
        public static final String NOT_REACHED = "notReached";
        public static final String RESOLVED = "resolved";
        public static final String FAILED = "failed";

        private Codes() {
        }
    }

    private static final PqcExplanationStepOutcome[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    PqcExplanationStepOutcome(String code, String label, String description) {
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
    public static PqcExplanationStepOutcome findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown PQC explanation step outcome {}", code)));
    }
}

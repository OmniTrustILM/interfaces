package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * Where a CBOM Repository entry that the sync could not store stands. Later sync runs retry such an entry until its
 * retry budget (the platform setting {@code cbomSyncSkippedRetryRuns}) is spent; the entry is then written off and
 * stays listed for the retention ({@code cbomSyncSkipRetentionDays}) after its last attempt, so an operator can see
 * what was given up on and why, and can ask for another attempt.
 *
 * <p>
 * Being written off ends the retrying, not the attempting: a run no longer picks the entry up from this list, but it
 * still tries the document whenever the repository's listing offers it again, and records what happens on the same
 * entry. That is what the retention counts from -- a document the repository keeps offering keeps failing and keeps its
 * place on the list, while one the repository no longer offers ages out.
 */
@Schema(enumAsRef = true)
public enum CbomSyncSkipState implements IPlatformEnum {

    RETRYING(Codes.RETRYING, "Retrying", "The next sync runs try the entry again"),
    PERMANENTLY_SKIPPED(Codes.PERMANENTLY_SKIPPED, "Permanently skipped",
            "The retry budget is spent, so no run picks the entry up to retry it; a run still tries the document if "
                    + "the repository offers it again, and an operator can ask for a new budget");

    public static class Codes {
        public static final String RETRYING = "retrying";
        public static final String PERMANENTLY_SKIPPED = "permanentlySkipped";

        private Codes() {
        }
    }

    private static final CbomSyncSkipState[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CbomSyncSkipState(String code, String label, String description) {
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
    public static CbomSyncSkipState findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown CBOM sync skip state {}", code)));
    }
}

package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * The rung a signature has reached on the level ladder, named by what the rung adds rather than by any one format's
 * letters. Values are declared lowest to highest, so they compare directly as a ceiling, and every rung contains the
 * one below it.
 *
 * <p>
 * A formatting connector declares the name its family gives each rung. The platform shows that name wherever an
 * operator configures or reads a profile.
 * </p>
 */
@Schema(enumAsRef = true,
        description = "Level a signature has reached, ordered from SIGNED up to ARCHIVAL with each level containing "
                + "the one before it")
public enum SignatureLevel implements IPlatformEnum {

    SIGNED(Codes.SIGNED, "Signed", "A baseline signature"),
    TIMESTAMPED(Codes.TIMESTAMPED, "Timestamped",
            "A signature timestamp proves the signature existed at a point in time"),
    LONG_TERM(Codes.LONG_TERM, "Long Term",
            "Embedded validation material, so the signature validates offline without the issuers"),
    ARCHIVAL(Codes.ARCHIVAL, "Archival", "An archive timestamp extends validity past the algorithms' lifetime");

    public static class Codes {

        private Codes() {
        }

        public static final String SIGNED = "signed";
        public static final String TIMESTAMPED = "timestamped";
        public static final String LONG_TERM = "long_term";
        public static final String ARCHIVAL = "archival";
    }

    private static final SignatureLevel[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SignatureLevel(String code, String label, String description) {
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

    /**
     * Whether this rung is at or below the given ceiling. Rungs form a prefix ladder, so a profile or connector that
     * reaches {@code ceiling} reaches every rung below it too.
     *
     * <p>
     * A null ceiling is a value rather than a programming error: it designates no ceiling at all, so no rung is within
     * it and every caller is answered {@code false}.
     * </p>
     */
    public boolean isWithin(SignatureLevel ceiling) {
        return ceiling != null && this.ordinal() <= ceiling.ordinal();
    }

    @JsonCreator
    public static SignatureLevel findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown signature level {}", code)));
    }
}

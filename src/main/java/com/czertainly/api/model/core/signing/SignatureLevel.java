package com.czertainly.api.model.core.signing;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Conformance level of an Advanced Electronic Signature, following the ETSI baseline
 * profile terminology (ETSI EN 319 122 / 132 / 102 / TS 119 182).
 *
 * <ul>
 *   <li>B – Baseline: signature value and signing certificate only.</li>
 *   <li>T – Timestamp: adds a trusted timestamp over the signature value.</li>
 *   <li>LT – Long-Term: embeds revocation data and the full certificate chain so the
 *       signature can be validated without network access.</li>
 *   <li>LTA – Long-Term with Archive timestamp: adds a periodically renewed archive
 *       timestamp to protect against algorithm obsolescence.</li>
 * </ul>
 */
@Schema(enumAsRef = true)
public enum SignatureLevel implements IPlatformEnum {

    B(Codes.B, "B", "Baseline level – signature value and signing certificate only"),
    T(Codes.T, "T", "Timestamp level – adds a trusted timestamp over the signature"),
    LT(Codes.LT, "LT", "Long-Term level – embeds revocation data and full certificate chain"),
    LTA(Codes.LTA, "LTA", "Long-Term with Archive timestamp – adds periodically renewed archive timestamp"),
    ;

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

    @JsonCreator
    public static SignatureLevel findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signature level {}", code)));
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
        public static final String B = "B";
        public static final String T = "T";
        public static final String LT = "LT";
        public static final String LTA = "LTA";

        private Codes() {
        }
    }
}

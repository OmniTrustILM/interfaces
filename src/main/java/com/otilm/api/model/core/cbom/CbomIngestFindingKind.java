package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * What one row of a CBOM's cryptographic asset ingest report says: something the ingest had to tell the producer about
 * the document, or a component it could turn into no asset at all.
 */
@Schema(enumAsRef = true)
public enum CbomIngestFindingKind implements IPlatformEnum {

    FINDING(Codes.FINDING, "Finding",
            "Something the ingest had to tell the producer: a dropped value, a withheld digest, an ambiguous reference"),
    SKIP(Codes.SKIP, "Skip", "A component that yielded no asset at all, named with the reason");

    public static class Codes {
        public static final String FINDING = "finding";
        public static final String SKIP = "skip";

        private Codes() {
        }
    }

    private static final CbomIngestFindingKind[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CbomIngestFindingKind(String code, String label, String description) {
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
    public static CbomIngestFindingKind findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown CBOM ingest finding kind {}", code)));
    }
}

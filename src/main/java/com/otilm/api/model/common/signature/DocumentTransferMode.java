package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * Which transport a document transfer uses, and the discriminator selecting its shape.
 */
@Schema(enumAsRef = true)
public enum DocumentTransferMode implements IPlatformEnum {

    INLINE(Codes.INLINE, "Inline", "The document travels inline"),
    DIGEST_ONLY(Codes.DIGEST_ONLY, "Digest only",
            "Only the document's digest travels, so the document never enters the platform");

    public static class Codes {

        private Codes() {
        }

        public static final String INLINE = "inline";
        public static final String DIGEST_ONLY = "digestOnly";
    }

    private static final DocumentTransferMode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    DocumentTransferMode(String code, String label, String description) {
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
    public static DocumentTransferMode findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown document transfer mode {}", code)));
    }
}

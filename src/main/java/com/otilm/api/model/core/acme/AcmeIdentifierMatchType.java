package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How a pre-authorized identifier entry on an ACME profile is compared against an ordered identifier. Explicit match
 * types rather than globs or expressions, so what a policy covers can be read off the entry.
 */
@Schema(enumAsRef = true)
public enum AcmeIdentifierMatchType implements IPlatformEnum {
    EXACT("exact", "Exact"),
    SUBDOMAIN("subdomain", "Subdomain");

    private static final AcmeIdentifierMatchType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;

    AcmeIdentifierMatchType(String code, String label) {
        this.code = code;
        this.label = label;
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
        return null;
    }

    @JsonCreator
    public static AcmeIdentifierMatchType findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown ACME Identifier Match Type code {}", code)));
    }
}

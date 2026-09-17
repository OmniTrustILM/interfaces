package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * The kind of ACME identifier a pre-authorized entry stands for. The entry names one and only one, because a value
 * alone does not say which: {@code 192.0.2.1} reads as a DNS name as readily as an address literal, and an entry that
 * matched both would cover more than the operator who wrote it can see.
 *
 * <p>
 * The codes are the identifier types of RFC 8555 section 9.7.7 and RFC 8738, so an entry's type is written the way the
 * order that has to match it writes its own.
 */
@Schema(enumAsRef = true)
public enum AcmeIdentifierType implements IPlatformEnum {
    DNS("dns", "DNS"),
    IP("ip", "IP");

    private static final AcmeIdentifierType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;

    AcmeIdentifierType(String code, String label) {
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
    public static AcmeIdentifierType findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown ACME Identifier Type code {}", code)));
    }
}

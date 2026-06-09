package com.otilm.api.model.core.cmp;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CmpTransactionState implements IPlatformEnum {
    CERT_ISSUED("cert_issued", "Certificate issued"),
    CERT_REKEYED("cert_rekeyed", "Certificate re-keyed"),
    CERT_CONFIRMED("cert_confirmed", "Certificate confirmed"),
    CERT_REVOKED("cert_revoked", "Certificate revoked"),
    FAILED("failed", "Failed", "Any error occurred");

    private static final CmpTransactionState[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CmpTransactionState(String code, String label) {
        this(code, label,null);
    }

    CmpTransactionState(String code, String label, String description) {
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
    public static CmpTransactionState findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown cmp-transaction state {}", code)));
    }
}

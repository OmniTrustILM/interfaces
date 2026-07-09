package com.otilm.api.model.core.certificate;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CertificateRegistrationState implements IPlatformEnum {
    ACTIVE("active", "Active", "Registration is active; the challenge gates issue/rekey"),
    EXPIRED("expired", "Expired", "The issuance window has passed"),
    LOCKED("locked", "Locked", "Locked after too many failed challenge attempts"),
    CLOSED("closed", "Closed", "Behaves as unregistered (operator-closed or terminal)");

    private static final CertificateRegistrationState[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CertificateRegistrationState(String code, String label, String description) {
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
    public static CertificateRegistrationState findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown certificate registration state {}", code)));
    }
}

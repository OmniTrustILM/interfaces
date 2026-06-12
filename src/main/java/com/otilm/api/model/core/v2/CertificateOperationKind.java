package com.otilm.api.model.core.v2;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * The kind of certificate operation whose support an authority advertises via
 * {@link OperationSupport}. Closed set — modeled as an enum (rather than a raw String)
 * so consumers can match exhaustively and typos fail fast, consistent with the
 * {@link com.otilm.api.model.connector.v3.certificate.CertificateOperationStatus} enum.
 *
 * <p>This is the operator-API wire vocabulary. Core's
 * {@code com.czertainly.core.service.handler.authority.CertificateOperation} is the
 * matching domain enum (it additionally carries state-machine mappings) and stays in
 * Core — the contract module holds only the thin value-type.</p>
 */
@Schema(enumAsRef = true)
public enum CertificateOperationKind implements IPlatformEnum {

    ISSUE("ISSUE", "Issue", "Issue a new certificate"),
    RENEW("RENEW", "Renew", "Renew an existing certificate"),
    REVOKE("REVOKE", "Revoke", "Revoke a certificate"),
    REGISTER("REGISTER", "Register", "Register (pre-enroll) a certificate before issuance");

    private final String code;
    private final String label;
    private final String description;

    CertificateOperationKind(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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
        return description;
    }

    @JsonCreator
    public static CertificateOperationKind findByCode(String code) {
        return Arrays.stream(values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown certificate operation kind {}", code)));
    }
}

package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * How a connector treats each item of a sign request. Under {@link #PREHASHED_DIGEST} the connector never hashes the
 * item again: RSA PKCS#1 v1.5 wraps the digest in a {@code DigestInfo} encoded per RFC 8017 section 9.2 with explicit
 * NULL digest parameters, RSA-PSS uses the salt and MGF1 parameters the signature algorithm code fixes, and ECDSA signs
 * the digest as given. A connector that does not implement the mode answers 422 {@code PARAMETER_UNSUPPORTED} and never
 * ignores the field.
 */
@Schema(name = "SignatureInputMode", enumAsRef = true,
        description = "Whether each data item is the content to sign or already the digest of it")
public enum SignatureInputMode implements IPlatformEnum {

    DATA("data", "Data",
            "Sign the data as given; the connector hashes it with the digest the signature algorithm fixes"),
    PREHASHED_DIGEST("prehashedDigest", "Prehashed digest",
            "Each item is already the digest the signature algorithm fixes; the connector signs it without hashing again");

    private final String code;
    private final String label;
    private final String description;

    SignatureInputMode(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SignatureInputMode findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(mode -> mode.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown signature input mode {}", code)));
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
}

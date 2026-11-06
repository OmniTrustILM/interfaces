package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * How the connector, which is the signer, treats each data item of a sign request. The operation that takes the mode
 * states the obligations of {@link #PREHASHED_DIGEST}, so this component says only what each value means.
 */
@Schema(name = "SignatureInputMode", enumAsRef = true,
        description = "Whether each data item is the data itself or its digest. A hash-then-sign algorithm computes "
                + "a digest of the data and signs that digest, while a pure algorithm signs the data itself. The "
                + "platform sends prehashedDigest for raw signing, where its caller has already computed the digest")
public enum SignatureInputMode implements IPlatformEnum {

    DATA("data", "Data", "Each item is the data; a hash-then-sign algorithm computes its digest before signing"),
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

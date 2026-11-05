package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * How a signer treats each item it is given: as data to sign, or as a digest already computed. It sits with the other
 * algorithm enums so that every API naming an input mode, the connector sign request and the platform's own signing API
 * alike, uses the same codes. The obligations of {@link #PREHASHED_DIGEST} are stated by each operation that takes the
 * mode, so this component says only what each value means.
 */
@Schema(name = "SignatureInputMode", enumAsRef = true,
        description = "Whether each data item is the content to sign or already the digest the signature "
                + "algorithm fixes")
public enum SignatureInputMode implements IPlatformEnum {

    DATA("data", "Data", "Sign the data as given, applying the signature algorithm to it in full"),
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

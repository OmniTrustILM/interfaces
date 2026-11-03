package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * How a connector treats each item of a sign request. The conformance rules for {@link #PREHASHED_DIGEST} are the
 * schema description of this type.
 * <p>
 * {@code SignatureAlgorithm#getDigestAlgorithmIdentifier()} returns the digest identifier with absent parameters (the
 * CMS form), while the RSA PKCS#1 v1.5 {@code DigestInfo} needs explicit NULL parameters, so build it with
 * {@code DERNull.INSTANCE}.
 */
@Schema(name = "SignatureInputMode", enumAsRef = true,
        description = "Whether each data item is the content to sign or already the digest of it. Under "
                + "prehashedDigest each item is already the digest the selected signature algorithm fixes and is "
                + "never hashed again. RSA PKCS#1 v1.5 wraps it in a DigestInfo per RFC 8017 section 9.2 with "
                + "explicit NULL parameters; RSA-PSS and ECDSA sign it with the parameters the signature algorithm "
                + "code fixes. prehashedDigest is defined for RSA PKCS#1 v1.5, RSA-PSS and ECDSA signature "
                + "algorithm codes only: any other code answers 422 PARAMETER_UNSUPPORTED, and an item whose "
                + "length is not the output length of the fixed digest answers 422 VALIDATION_FAILED. A connector "
                + "that does not implement the mode answers 422 PARAMETER_UNSUPPORTED and never ignores the field; "
                + "one that implements it advertises the prehashedSigning feature flag.")
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

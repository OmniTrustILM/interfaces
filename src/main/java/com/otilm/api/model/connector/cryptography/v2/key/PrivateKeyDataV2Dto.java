package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.otilm.api.model.common.cryptography.KeyAlgorithmIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Non-exportable private-key descriptor without key material.
 */
@Schema(name = "PrivateKeyDataV2Dto",
        description = "Private-key descriptor without key material, which names the key's parameters by their "
                + "AlgorithmIdentifier when the connector can read them",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
@JsonPropertyOrder({"type", "algorithm", "length", "algorithmIdentifier", "metadata"})
@ToString(callSuper = true)
public final class PrivateKeyDataV2Dto extends KeyDataV2Dto {

    @Getter
    @Setter
    @Schema(description = "Key length in bits. The connector must state it when `algorithmIdentifier` carries no "
            + "size, as for RSA. The platform reads the size from the public key or from `algorithmIdentifier` first.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    @Min(value = 1, message = "key length must be positive")
    private Integer length;

    @ToString.Exclude
    private AlgorithmIdentifier algorithmIdentifier;

    public PrivateKeyDataV2Dto() {
        super(KeyTypeV2.PRIVATE);
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class,
            allowableValues = "Private", _const = "Private", requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyTypeV2 getType() {
        return super.getType();
    }

    @Schema(description = "Base64-encoded DER AlgorithmIdentifier naming the key's parameters, as SubjectPublicKeyInfo "
            + "and PKCS#8 carry it. The connector must send it when it can read the parameters, and in a key pair it "
            + "must equal the AlgorithmIdentifier of `publicKeySpki` byte for byte.", type = "string", format = "byte",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public byte[] getAlgorithmIdentifier() {
        return algorithmIdentifier == null ? null : encoded(algorithmIdentifier);
    }

    @JsonSetter("algorithmIdentifier")
    public void setAlgorithmIdentifier(byte[] algorithmIdentifier) {
        if (algorithmIdentifier == null) {
            this.algorithmIdentifier = null;
            return;
        }
        try {
            this.algorithmIdentifier = KeyAlgorithmIdentifiers.read(algorithmIdentifier);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("algorithmIdentifier is invalid: " + e.getMessage(), e);
        }
    }

    boolean carriesTheIdentifierOf(PublicKeyDataV2Dto publicKey) {
        if (algorithmIdentifier == null) {
            return true;
        }
        return publicKey
                .spkiAlgorithm()
                .map(spkiAlgorithm -> Arrays.equals(getAlgorithmIdentifier(), encoded(spkiAlgorithm)))
                .orElse(true);
    }

    private static byte[] encoded(AlgorithmIdentifier identifier) {
        try {
            return identifier.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

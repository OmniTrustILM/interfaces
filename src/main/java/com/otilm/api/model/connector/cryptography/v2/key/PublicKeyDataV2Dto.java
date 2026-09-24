package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import lombok.ToString;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MLDSAPublicKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusPublicKeyParameters;

/**
 * Public-key descriptor whose sole permitted key representation is DER SubjectPublicKeyInfo. The
 * {@link com.otilm.api.model.common.enums.cryptography.KeyAlgorithm#UNKNOWN} algorithm accepts any parseable public
 * SPKI because no concrete algorithm family is available for comparison.
 */
@Schema(name = "PublicKeyDataV2Dto",
        description = "Public-key descriptor with an SPKI representation. "
                + "Algorithm Unknown accepts any parseable public SPKI and skips algorithm-family matching.",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
@ToString(callSuper = true)
public final class PublicKeyDataV2Dto extends KeyDataV2Dto {

    @ToString.Exclude
    private byte[] publicKeySpki;

    public PublicKeyDataV2Dto() {
        super(KeyTypeV2.PUBLIC);
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class, allowableValues = "Public",
            _const = "Public", requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyTypeV2 getType() {
        return super.getType();
    }

    @Schema(description = "Base64-encoded DER SubjectPublicKeyInfo.", type = "string", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "publicKeySpki is required")
    public byte[] getPublicKeySpki() {
        return publicKeySpki == null ? null : publicKeySpki.clone();
    }

    @JsonSetter("publicKeySpki")
    public void setPublicKeySpki(byte[] publicKeySpki) {
        if (publicKeySpki == null) {
            this.publicKeySpki = null;
            return;
        }

        try {
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKeySpki));
            byte[] canonicalDer = spki.getEncoded(ASN1Encoding.DER);
            if (!Arrays.equals(publicKeySpki, canonicalDer)) {
                throw new IllegalArgumentException("publicKeySpki must use canonical DER encoding");
            }
            AsymmetricKeyParameter parsedKey = parsePublicKey(spki);
            if (parsedKey.isPrivate()) {
                throw new IllegalArgumentException("publicKeySpki contains private key material");
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "publicKeySpki must contain a valid public key in DER SubjectPublicKeyInfo", e);
        }
        this.publicKeySpki = publicKeySpki.clone();
    }

    boolean matchesDeclaredAlgorithm() {
        if (publicKeySpki == null || getAlgorithm() == null) {
            return true;
        }

        try {
            AsymmetricKeyParameter parsedKey = parsePublicKey(
                    SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKeySpki)));
            return matchesDeclaredAlgorithm(parsedKey);
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "publicKeySpki does not match the declared key algorithm")
    public boolean isPublicKeySpkiMatchingDeclaredAlgorithm() {
        return matchesDeclaredAlgorithm();
    }

    @SuppressWarnings("deprecation")
    private boolean matchesDeclaredAlgorithm(AsymmetricKeyParameter key) {
        return switch (getAlgorithm()) {
            case RSA -> key instanceof RSAKeyParameters;
            case ECDSA -> key instanceof ECPublicKeyParameters;
            case FALCON -> key instanceof FalconPublicKeyParameters;
            case MLDSA -> key instanceof MLDSAPublicKeyParameters;
            case SLHDSA -> key instanceof SLHDSAPublicKeyParameters;
            case MLKEM -> key instanceof MLKEMPublicKeyParameters;
            case DILITHIUM -> key instanceof DilithiumPublicKeyParameters;
            case SPHINCSPLUS -> key instanceof SPHINCSPlusPublicKeyParameters;
            case UNKNOWN -> true;
        };
    }

    private static AsymmetricKeyParameter parsePublicKey(SubjectPublicKeyInfo spki) throws IOException {
        try {
            return PublicKeyFactory.createKey(spki);
        } catch (IOException | IllegalArgumentException standardFailure) {
            try {
                return org.bouncycastle.pqc.crypto.util.PublicKeyFactory.createKey(spki);
            } catch (IOException | IllegalArgumentException pqcFailure) {
                pqcFailure.addSuppressed(standardFailure);
                if (pqcFailure instanceof IOException ioException) {
                    throw ioException;
                }
                throw new IOException("Unsupported or malformed SubjectPublicKeyInfo", pqcFailure);
            }
        }
    }
}

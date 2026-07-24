package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusPublicKeyParameters;

import java.io.IOException;
import java.util.Arrays;

/**
 * Public-key descriptor whose sole permitted key representation is DER SubjectPublicKeyInfo.
 */
@Schema(name = "PublicKeyDataV2Dto", description = "Public-key descriptor with an optional SPKI representation",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class PublicKeyDataV2Dto extends KeyDataV2Dto {

    @ToString.Exclude
    private byte[] publicKeySpki;

    public PublicKeyDataV2Dto() {
        super(KeyRoleV2.PUBLIC);
    }

    @Override
    protected KeyRoleV2 expectedType() {
        return KeyRoleV2.PUBLIC;
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class,
            allowableValues = "Public", _const = "Public",
            requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyRoleV2 getType() {
        return super.getType();
    }

    @Schema(description = "Base64-encoded DER SubjectPublicKeyInfo. Omitted when public representation was not requested.",
            type = "string", format = "byte", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(
                    ASN1Primitive.fromByteArray(publicKeySpki));
            byte[] canonicalDer = spki.getEncoded(ASN1Encoding.DER);
            if (!Arrays.equals(publicKeySpki, canonicalDer)) {
                throw new IllegalArgumentException("publicKeySpki must use canonical DER encoding");
            }
            AsymmetricKeyParameter parsedKey = parsePublicKey(spki);
            if (parsedKey.isPrivate()) {
                throw new IllegalArgumentException("publicKeySpki contains private key material");
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("publicKeySpki must contain a valid public key in DER SubjectPublicKeyInfo", e);
        }
        this.publicKeySpki = publicKeySpki.clone();
    }

    @Override
    protected void validateRoleSpecificData() {
        if (publicKeySpki == null) {
            return;
        }

        try {
            AsymmetricKeyParameter parsedKey = parsePublicKey(
                    SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKeySpki)));
            if (!matchesDeclaredAlgorithm(parsedKey)) {
                throw new IllegalArgumentException("publicKeySpki does not match the declared key algorithm");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("publicKeySpki does not contain a valid public key", e);
        }
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
            case AES -> false;
        };
    }

    private static AsymmetricKeyParameter parsePublicKey(SubjectPublicKeyInfo spki) throws IOException {
        try {
            return PublicKeyFactory.createKey(spki);
        } catch (IOException | IllegalArgumentException standardFailure) {
            try {
                return org.bouncycastle.pqc.crypto.util.PublicKeyFactory.createKey(spki);
            } catch (IOException | IllegalArgumentException legacyFailure) {
                legacyFailure.addSuppressed(standardFailure);
                if (legacyFailure instanceof IOException ioException) {
                    throw ioException;
                }
                throw new IOException("Unsupported or malformed SubjectPublicKeyInfo", legacyFailure);
            }
        }
    }
}

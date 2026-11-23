package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.otilm.api.model.common.cryptography.ExplicitCurve;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.ToString;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
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

@Schema(name = "PublicKeyDataV2Dto",
        description = "Public-key descriptor carrying the public key as DER SubjectPublicKeyInfo. "
                + "Algorithm Unknown accepts any parseable public SPKI and skips algorithm-family matching.",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
@ToString(callSuper = true)
public final class PublicKeyDataV2Dto extends KeyDataV2Dto {

    private static final String INVALID_SPKI = "publicKeySpki must contain a valid public key in DER "
            + "SubjectPublicKeyInfo";
    private static final String CURVE_PAST_THE_BOUND = "publicKeySpki must state an explicit curve within the size "
            + "of the largest named curve";

    @ToString.Exclude
    private byte[] publicKeySpki;

    @ToString.Exclude
    private SubjectPublicKeyInfo subjectPublicKeyInfo;

    @ToString.Exclude
    private AsymmetricKeyParameter publicKey;

    public PublicKeyDataV2Dto() {
        super(KeyTypeV2.PUBLIC);
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class, allowableValues = "Public",
            _const = "Public", requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyTypeV2 getType() {
        return super.getType();
    }

    @Schema(description = "Public key of `algorithm` as base64-encoded DER SubjectPublicKeyInfo", type = "string",
            format = "byte", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "publicKeySpki is required")
    public byte[] getPublicKeySpki() {
        return publicKeySpki == null ? null : publicKeySpki.clone();
    }

    @JsonSetter("publicKeySpki")
    public void setPublicKeySpki(byte[] publicKeySpki) {
        if (publicKeySpki == null) {
            this.publicKeySpki = null;
            this.subjectPublicKeyInfo = null;
            this.publicKey = null;
            return;
        }

        SubjectPublicKeyInfo parsed = parseCanonical(publicKeySpki);
        if (ExplicitCurve.pastTheBound(parsed.getAlgorithm())) {
            throw new IllegalArgumentException(CURVE_PAST_THE_BOUND);
        }
        AsymmetricKeyParameter key = requirePublicKey(parsed);
        this.publicKeySpki = publicKeySpki.clone();
        this.subjectPublicKeyInfo = parsed;
        this.publicKey = key;
    }

    /**
     * @deprecated connectors released before 2.21.0 send the length beside the SPKI. Length is ignored because SPKI
     * states the length itself.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    @JsonSetter("length")
    @Schema(description = "Key length in bits, as connectors released before 2.21.0 send it; ignored, since the "
            + "platform reads the size from `publicKeySpki`", type = "integer", format = "int32",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE,
            deprecated = true)
    public void setLength(Integer length) {
        // publicKeySpki states the size.
    }

    boolean matchesDeclaredAlgorithm() {
        if (subjectPublicKeyInfo == null || getAlgorithm() == null) {
            return true;
        }
        return matchesDeclaredAlgorithm(publicKey);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "publicKeySpki does not match the declared key algorithm")
    public boolean isPublicKeySpkiMatchingDeclaredAlgorithm() {
        return matchesDeclaredAlgorithm();
    }

    Optional<AlgorithmIdentifier> spkiAlgorithm() {
        return Optional.ofNullable(subjectPublicKeyInfo).map(SubjectPublicKeyInfo::getAlgorithm);
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
            case AES -> false;
            case UNKNOWN -> true;
        };
    }

    private static SubjectPublicKeyInfo parseCanonical(byte[] encoded) {
        try {
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(encoded));
            if (!Arrays.equals(encoded, spki.getEncoded(ASN1Encoding.DER))) {
                throw new IllegalArgumentException("publicKeySpki must use canonical DER encoding");
            }
            return spki;
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_SPKI, e);
        }
    }

    private static AsymmetricKeyParameter requirePublicKey(SubjectPublicKeyInfo spki) {
        try {
            AsymmetricKeyParameter key = parsePublicKey(spki);
            if (key.isPrivate()) {
                throw new IllegalArgumentException("publicKeySpki contains private key material");
            }
            return key;
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_SPKI, e);
        }
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

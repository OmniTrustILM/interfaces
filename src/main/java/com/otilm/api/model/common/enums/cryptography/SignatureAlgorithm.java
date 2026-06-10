package com.otilm.api.model.common.enums.cryptography;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SignatureAlgorithm implements IPlatformEnum {
    SHA256_WITH_RSA(
            "SHA256withRSA", "RSASSA-PKCS_v1.5 using SHA256", "RSA signature with SHA-256 digest",
            new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256), false
    ),
    SHA384_WITH_RSA(
            "SHA384withRSA", "RSASSA-PKCS_v1.5 using SHA384", "RSA signature with SHA-384 digest",
            new AlgorithmIdentifier(PKCSObjectIdentifiers.sha384WithRSAEncryption, DERNull.INSTANCE),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384), false
    ),
    SHA512_WITH_RSA(
            "SHA512withRSA", "RSASSA-PKCS_v1.5 using SHA512", "RSA signature with SHA-512 digest",
            new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), false
    ),
    SHA256_WITH_RSA_PSS(
            "SHA256withRSAandMGF1", "RSASSA-PSS using SHA256", "RSA-PSS signature with SHA-256 digest",
            createPssAlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, 32),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256), false
    ),
    SHA384_WITH_RSA_PSS(
            "SHA384withRSAandMGF1", "RSASSA-PSS using SHA384", "RSA-PSS signature with SHA-384 digest",
            createPssAlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, 48),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384), false
    ),
    SHA512_WITH_RSA_PSS(
            "SHA512withRSAandMGF1", "RSASSA-PSS using SHA512", "RSA-PSS signature with SHA-512 digest",
            createPssAlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, 64),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), false
    ),
    SHA256_WITH_ECDSA(
            "SHA256withECDSA", "ECDSA using SHA256", "ECDSA signature with SHA-256 digest",
            new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256), false
    ),
    SHA384_WITH_ECDSA(
            "SHA384withECDSA", "ECDSA using SHA384", "ECDSA signature with SHA-384 digest",
            new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA384),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384), false
    ),
    SHA512_WITH_ECDSA(
            "SHA512withECDSA", "ECDSA using SHA512", "ECDSA signature with SHA-512 digest",
            new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), false
    ),
    ED25519(
            "Ed25519", "Pure EdDSA with Edwards25519", "Edwards-curve DSA with Curve25519",
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.101.112")),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), true
    ),
    ED448(
            "Ed448", "Pure EdDSA with Edwards448", "Edwards-curve DSA with Curve448",
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.101.113")),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256_len, new ASN1Integer(512)), true
    ),
    FALCON_1024(
            "FALCON-1024", "FALCON-1024", "Post-quantum FALCON-1024 lattice-based signature scheme",
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.9999.3.14")),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), true
    ),
    ML_DSA_65(
            "ML-DSA-65", "ML-DSA-65 (Dilithium)", "Post-quantum Module-Lattice-Based digital signature, NIST FIPS 204 level 3",
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512), true
    ),
    SLH_DSA_SHA2_128F(
            "SLH-DSA-SHA2-128F", "SLH-DSA-SHA2-128F (SPHINCS+)", "Post-quantum stateless hash-based signature, NIST FIPS 205 SHA2-128F",
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_slh_dsa_sha2_128f),
            new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256), true
    );

    private static final SignatureAlgorithm[] VALUES;

    static {
        VALUES = values();
    }

    private static AlgorithmIdentifier createPssAlgorithmIdentifier(ASN1ObjectIdentifier hashOid, int saltLength) {
        AlgorithmIdentifier hashAlgId = new AlgorithmIdentifier(hashOid, DERNull.INSTANCE);
        AlgorithmIdentifier mgfAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, hashAlgId);
        RSASSAPSSparams pssParams = new RSASSAPSSparams(hashAlgId, mgfAlgId, new ASN1Integer(saltLength), RSASSAPSSparams.DEFAULT_TRAILER_FIELD);
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, pssParams);
    }

    @Schema(description = "Signature algorithm code",
            examples = {"SHA256withECDSA"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;
    @Getter
    private final AlgorithmIdentifier algorithmIdentifier;
    @Getter
    private final AlgorithmIdentifier digestAlgorithmIdentifier;
    @Getter
    private final boolean digestAlgorithmIsImplicit;

    SignatureAlgorithm(String code, String label, String description, AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier digestAlgorithmIdentifier, boolean digestAlgorithmIsImplicit) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.algorithmIdentifier = algorithmIdentifier;
        this.digestAlgorithmIdentifier = digestAlgorithmIdentifier;
        this.digestAlgorithmIsImplicit = digestAlgorithmIsImplicit;
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
    public static SignatureAlgorithm findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signature algorithm code {}", code)));
    }
}

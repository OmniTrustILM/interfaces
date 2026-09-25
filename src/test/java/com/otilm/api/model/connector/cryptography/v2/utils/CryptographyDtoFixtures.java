package com.otilm.api.model.connector.cryptography.v2.utils;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PrivateKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PrivateKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PublicKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PublicKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.material.EncryptedKeyMaterialV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class CryptographyDtoFixtures {

    public static final String KEY_REFERENCE = "1c9a7e58-33d2-4b0a-8f4e-6a91d27c5f10";

    public static final String EXPORT_PASSPHRASE = "correct horse battery staple";

    private static final String METADATA_UUID = "00000000-0000-0000-0000-000000000001";
    private static final String RSA_2048_PUBLIC_KEY_SPKI_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvdLbQTpr/mGvt4TmMcc4+eE4+ZzNU6ju4EZ33zkmNz5pPmHK+"
            + "r8LuKQLMTgIRp89xQMePPk0mTo/xiAzH9Oedej6JpdHr8dc96JgD/mVqu2GTj02bABV4One/tmDHzXGXf3dchT9HdYWOD"
            + "QmTk0ktQ2CdyASffiCelZOLLGdtZgGDVvPlvU+45sDqRVG3h4I/L3pnzgV6TJf6Rrh0HZEuBI+e7VVqUvbvPOIWGBiM+B"
            + "EEjUlUwYAkCKH1btk25hGejipeB/haHejOdvukQhPd85m5/94wz5+x4XiSTK5itd+zoYhVTXruLUrL4rC5hAYJ+y5RTuo"
            + "ZQDoM7cVebZG6wIDAQAB";

    private CryptographyDtoFixtures() {
    }

    public static MetadataAttributeV2 validMetadataAttribute() {
        MetadataAttributeV2 metadata = new MetadataAttributeV2();
        metadata.setUuid(METADATA_UUID);
        metadata.setName("provider handle");
        metadata.setContentType(AttributeContentType.STRING);
        metadata.setProperties(new MetadataAttributeProperties());
        metadata.setContent(List.of(new StringAttributeContentV2("provider-key-1")));
        return metadata;
    }

    public static List<MetadataAttribute> validMetadata() {
        return List.of(validMetadataAttribute());
    }

    public static <T extends TokenScopedRequestV2Dto> T withValidTokenScope(T dto) {
        dto.setTokenAttributes(List.of());
        return dto;
    }

    public static <T extends TokenProfileScopedRequestV2Dto> T withValidTokenProfileScope(T dto) {
        withValidTokenScope(dto);
        dto.setTokenProfileAttributes(List.of());
        return dto;
    }

    public static SecretKeyDataV2Dto validSecretKeyData() {
        SecretKeyDataV2Dto keyData = new SecretKeyDataV2Dto();
        keyData.setAlgorithm(KeyAlgorithm.RSA);
        keyData.setLength(2048);
        return keyData;
    }

    public static PrivateKeyDataV2Dto validPrivateKeyData() {
        PrivateKeyDataV2Dto keyData = new PrivateKeyDataV2Dto();
        keyData.setAlgorithm(KeyAlgorithm.RSA);
        keyData.setLength(2048);
        return keyData;
    }

    public static PublicKeyDataV2Dto validPublicKeyData() {
        PublicKeyDataV2Dto keyData = new PublicKeyDataV2Dto();
        keyData.setAlgorithm(KeyAlgorithm.RSA);
        keyData.setLength(2048);
        keyData.setPublicKeySpki(Base64.getDecoder().decode(RSA_2048_PUBLIC_KEY_SPKI_BASE64));
        return keyData;
    }

    public static SecretKeyDataResponseV2Dto validSecretKeyDataResponse() {
        SecretKeyDataResponseV2Dto response = new SecretKeyDataResponseV2Dto();
        response.setKeyMeta(validMetadata());
        response.setKeyData(validSecretKeyData());
        return response;
    }

    public static PrivateKeyDataResponseV2Dto validPrivateKeyDataResponse() {
        PrivateKeyDataResponseV2Dto response = new PrivateKeyDataResponseV2Dto();
        response.setKeyMeta(validMetadata());
        response.setKeyData(validPrivateKeyData());
        return response;
    }

    public static PublicKeyDataResponseV2Dto validPublicKeyDataResponse() {
        PublicKeyDataResponseV2Dto response = new PublicKeyDataResponseV2Dto();
        response.setKeyMeta(validMetadata());
        response.setKeyData(validPublicKeyData());
        return response;
    }

    public static KeyPairDataResponseV2Dto validDistinctLengthKeyPairResponse(KeyAlgorithm algorithm)
            throws GeneralSecurityException {
        KeyPairGenerator generator;
        int publicLength;
        int privateLength;
        switch (algorithm) {
            case ECDSA -> {
                generator = KeyPairGenerator.getInstance("EC");
                generator.initialize(new ECGenParameterSpec("secp256r1"));
                publicLength = 512;
                privateLength = 256;
            }
            case MLDSA -> {
                generator = KeyPairGenerator.getInstance("ML-DSA-65", new BouncyCastleProvider());
                publicLength = 15616;
                privateLength = 32256;
            }
            default -> throw new IllegalArgumentException("Unsupported key-pair length fixture: " + algorithm);
        }

        PublicKeyDataResponseV2Dto publicKey = validPublicKeyDataResponse();
        publicKey.getKeyData().setAlgorithm(algorithm);
        publicKey.getKeyData().setLength(publicLength);
        publicKey.getKeyData().setPublicKeySpki(generator.generateKeyPair().getPublic().getEncoded());
        PrivateKeyDataResponseV2Dto privateKey = validPrivateKeyDataResponse();
        privateKey.getKeyData().setAlgorithm(algorithm);
        privateKey.getKeyData().setLength(privateLength);

        KeyPairDataResponseV2Dto response = new KeyPairDataResponseV2Dto();
        response.setPublicKeyData(publicKey);
        response.setPrivateKeyData(privateKey);
        response.setKeyPairMeta(validMetadata());
        return response;
    }

    public static ExportKeyRequestV2Dto validExportKeyRequest() {
        ExportKeyRequestV2Dto request = withValidTokenProfileScope(new ExportKeyRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setKeyRequestType(KeyRequestType.KEY_PAIR);
        request.setKeyReference(KEY_REFERENCE);
        request.setExportKeyAttributes(List.of());
        request.setPassphrase(EXPORT_PASSPHRASE);
        return request;
    }

    public static ExportKeyResponseV2Dto validExportKeyResponse() {
        ExportKeyResponseV2Dto response = new ExportKeyResponseV2Dto();
        response.setMaterial(validEncryptedKeyMaterial());
        response.setKeyReference(KEY_REFERENCE);
        response.setKeyData(validPublicKeyData());
        return response;
    }

    public static EncryptedKeyMaterialV2Dto validEncryptedKeyMaterial() {
        EncryptedKeyMaterialV2Dto material = new EncryptedKeyMaterialV2Dto();
        material.setEncryptedPrivateKeyInfo(pinnedProtectionEnvelope());
        return material;
    }

    /**
     * A PKCS#8 EncryptedPrivateKeyInfo in the pinned protection profile. The ciphertext is filler because no contract
     * rule reads it: everything the platform and a connector verify lives in the algorithm identifier.
     */
    public static byte[] pinnedProtectionEnvelope() {
        AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
        KeyDerivationFunc keyDerivation = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2,
                new PBKDF2Params(new byte[16], 100_000, prf));
        EncryptionScheme scheme = new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CBC,
                new DEROctetString(new byte[16]));
        AlgorithmIdentifier protection = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2,
                new PBES2Parameters(keyDerivation, scheme));
        try {
            return new EncryptedPrivateKeyInfo(protection, new byte[32]).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new IllegalStateException("could not encode the protected key material fixture", e);
        }
    }
}

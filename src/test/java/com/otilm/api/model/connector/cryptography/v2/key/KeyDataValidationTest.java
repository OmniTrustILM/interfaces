package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyData;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class KeyDataValidationTest {

    private static final Provider BOUNCY_CASTLE = new BouncyCastleProvider();

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("validKeyDescriptors")
    void validate_hasNoViolations_forValidKeyDescriptor(KeyDataV2Dto keyData) {
        // given
        KeyDataV2Dto validDescriptor = keyData;

        // when
        Set<ConstraintViolation<KeyDataV2Dto>> violations = VALIDATOR.validate(validDescriptor);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<KeyDataV2Dto>> validKeyDescriptors() {
        return Stream
                .of(named("secret key", validSecretKeyData()), named("private key", validPrivateKeyData()),
                        named("public key", validPublicKeyData()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequiredKeyFields")
    void validate_hasExpectedViolation_forInvalidRequiredKeyField(InvalidKeyData invalidKeyData) {
        // given
        KeyDataV2Dto keyData = invalidKeyData.keyData();

        // when
        Set<ConstraintViolation<KeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertHasViolation(violations, invalidKeyData.path(), invalidKeyData.message());
    }

    static Stream<Named<InvalidKeyData>> invalidRequiredKeyFields() {
        return Stream
                .of(named("secret", validSecretKeyData()), named("private", validPrivateKeyData()),
                        named("public", validPublicKeyData()))
                .flatMap(namedDescriptor -> {
                    KeyDataV2Dto missingAlgorithm = copyOf(namedDescriptor.getPayload());
                    missingAlgorithm.setAlgorithm(null);
                    KeyDataV2Dto missingLength = copyOf(namedDescriptor.getPayload());
                    missingLength.setLength(null);
                    KeyDataV2Dto zeroLength = copyOf(namedDescriptor.getPayload());
                    zeroLength.setLength(0);
                    KeyDataV2Dto negativeLength = copyOf(namedDescriptor.getPayload());
                    negativeLength.setLength(-1);
                    String role = namedDescriptor.getName();
                    return Stream
                            .of(named(role + " missing algorithm",
                                    new InvalidKeyData(missingAlgorithm, "algorithm", "key algorithm is required")),
                                    named(role + " missing length",
                                            new InvalidKeyData(missingLength, "length", "key length is required")),
                                    named(role + " zero length",
                                            new InvalidKeyData(zeroLength, "length", "key length must be positive")),
                                    named(role + " negative length", new InvalidKeyData(negativeLength, "length",
                                            "key length must be positive")));
                });
    }

    @Test
    void validate_rejectsNullMetadataElement_atIndexedPath() {
        // given
        SecretKeyDataV2Dto keyData = validSecretKeyData();
        keyData.setMetadata(Collections.singletonList(null));

        // when
        Set<ConstraintViolation<SecretKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertHasViolation(violations, "metadata[0].<list element>", "key metadata must not contain null items");
    }

    @Test
    void validate_cascadesMetadataConstraint_atIndexedPath() {
        // given
        MetadataAttributeV2 metadataWithoutName = validMetadataAttribute();
        metadataWithoutName.setName(null);
        SecretKeyDataV2Dto keyData = validSecretKeyData();
        keyData.setMetadata(List.of(metadataWithoutName));

        // when
        Set<ConstraintViolation<SecretKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertHasViolation(violations, "metadata[0].<list element>.name", "name must not be blank");
    }

    @Test
    void validate_hasNoViolations_forMatchingRsaSpki() throws Exception {
        // given
        int declaredRsaLength = 2048;
        byte[] rsaSpki = generateRsaSpki(declaredRsaLength);
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(KeyAlgorithm.RSA);
        keyData.setLength(declaredRsaLength);
        keyData.setPublicKeySpki(rsaSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_hasNoViolations_forMatchingEcSpki() throws Exception {
        // given
        int declaredEcLength = 256;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        byte[] ecSpki = generator.generateKeyPair().getPublic().getEncoded();
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(KeyAlgorithm.ECDSA);
        keyData.setLength(declaredEcLength);
        keyData.setPublicKeySpki(ecSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("supportedPqcAlgorithms")
    void validate_hasNoViolations_forSupportedPqcSpki(PqcAlgorithm algorithm) throws Exception {
        // given
        int parameterSetLength = 1;
        byte[] pqcSpki = generateSpki(algorithm.generatorName());
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(algorithm.declaredAlgorithm());
        keyData.setLength(parameterSetLength);
        keyData.setPublicKeySpki(pqcSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertTrue(violations.isEmpty());
    }

    @SuppressWarnings("deprecation")
    static Stream<Named<PqcAlgorithm>> supportedPqcAlgorithms() {
        return Stream
                .of(named("FALCON", new PqcAlgorithm("FALCON-512", KeyAlgorithm.FALCON)),
                        named("ML-DSA-44", new PqcAlgorithm("ML-DSA-44", KeyAlgorithm.MLDSA)),
                        named("ML-DSA-65", new PqcAlgorithm("ML-DSA-65", KeyAlgorithm.MLDSA)),
                        named("ML-DSA-87", new PqcAlgorithm("ML-DSA-87", KeyAlgorithm.MLDSA)),
                        named("SLH-DSA-SHA2-128S", new PqcAlgorithm("SLH-DSA-SHA2-128S", KeyAlgorithm.SLHDSA)),
                        named("SLH-DSA-SHA2-128F", new PqcAlgorithm("SLH-DSA-SHA2-128F", KeyAlgorithm.SLHDSA)),
                        named("SLH-DSA-SHA2-192S", new PqcAlgorithm("SLH-DSA-SHA2-192S", KeyAlgorithm.SLHDSA)),
                        named("SLH-DSA-SHA2-192F", new PqcAlgorithm("SLH-DSA-SHA2-192F", KeyAlgorithm.SLHDSA)),
                        named("SLH-DSA-SHA2-256S", new PqcAlgorithm("SLH-DSA-SHA2-256S", KeyAlgorithm.SLHDSA)),
                        named("SLH-DSA-SHA2-256F", new PqcAlgorithm("SLH-DSA-SHA2-256F", KeyAlgorithm.SLHDSA)),
                        named("ML-KEM", new PqcAlgorithm("ML-KEM-512", KeyAlgorithm.MLKEM)),
                        named("CRYSTALS-Dilithium", new PqcAlgorithm("DILITHIUM2", KeyAlgorithm.DILITHIUM)),
                        named("SPHINCS+", new PqcAlgorithm("SPHINCS+-SHA2-128F", KeyAlgorithm.SPHINCSPLUS)));
    }

    @Test
    void validate_hasNoViolations_forUnknownAlgorithmWithParseablePublicSpki() throws Exception {
        // given
        int actualRsaLength = 2048;
        byte[] parseableSpki = generateRsaSpki(actualRsaLength);
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(KeyAlgorithm.UNKNOWN);
        keyData.setLength(actualRsaLength);
        keyData.setPublicKeySpki(parseableSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_rejectsSpki_forMismatchedDeclaredAlgorithm() throws Exception {
        // given
        int actualRsaLength = 2048;
        byte[] rsaSpki = generateRsaSpki(actualRsaLength);
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(KeyAlgorithm.ECDSA);
        keyData.setLength(actualRsaLength);
        keyData.setPublicKeySpki(rsaSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertHasViolation(violations, "publicKeySpkiMatchingDeclaredAlgorithm",
                "publicKeySpki does not match the declared key algorithm");
    }

    @Test
    void validate_rejectsSpki_forMismatchedDeclaredLength() throws Exception {
        // given
        int actualRsaLength = 2048;
        int mismatchedDeclaredLength = 3072;
        byte[] rsaSpki = generateRsaSpki(actualRsaLength);
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setAlgorithm(KeyAlgorithm.RSA);
        keyData.setLength(mismatchedDeclaredLength);
        keyData.setPublicKeySpki(rsaSpki);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(keyData);

        // then
        assertHasViolation(violations, "publicKeySpkiMatchingDeclaredLength",
                "publicKeySpki does not match the declared key length");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPublicKeyEncodings")
    void publicKeySpki_setterRejectsInvalidEncoding(byte[] invalidEncoding) {
        // given
        PublicKeyDataV2Dto keyData = validPublicKeyData();

        // when
        Executable setPublicKey = () -> keyData.setPublicKeySpki(invalidEncoding);

        // then
        assertThrows(IllegalArgumentException.class, setPublicKey);
    }

    static Stream<Named<byte[]>> invalidPublicKeyEncodings() throws Exception {
        int rsaLength = 2048;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(rsaLength);
        KeyPair keyPair = generator.generateKeyPair();
        byte[] canonicalSpki = keyPair.getPublic().getEncoded();
        ASN1Sequence sequence = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(canonicalSpki));
        byte[] nonCanonicalBer = new BERSequence(sequence.toArray()).getEncoded(ASN1Encoding.BER);
        return Stream
                .of(named("malformed ASN.1", new byte[]{1, 2, 3}), named("non-canonical BER", nonCanonicalBer),
                        named("PKCS8 private key", keyPair.getPrivate().getEncoded()));
    }

    @Test
    void publicKeySpki_setterCopiesCallerOwnedBytesAndToStringRedactsThem() throws Exception {
        // given
        int rsaLength = 2048;
        byte[] callerOwnedSpki = generateRsaSpki(rsaLength);
        byte[] expectedStoredSpki = callerOwnedSpki.clone();
        String firstBytesMarker = Arrays.toString(callerOwnedSpki);
        PublicKeyDataV2Dto keyData = validPublicKeyData();
        keyData.setLength(rsaLength);
        keyData.setPublicKeySpki(callerOwnedSpki);

        // when
        callerOwnedSpki[0] = (byte) (callerOwnedSpki[0] + 1);
        byte[] storedSpki = keyData.getPublicKeySpki();
        String representation = keyData.toString();

        // then
        assertArrayEquals(expectedStoredSpki, storedSpki);
        assertFalse(representation.contains(firstBytesMarker));
    }

    private static byte[] generateRsaSpki(int keyLength) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keyLength);
        return generator.generateKeyPair().getPublic().getEncoded();
    }

    private static byte[] generateSpki(String algorithm) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, BOUNCY_CASTLE);
        return generator.generateKeyPair().getPublic().getEncoded();
    }

    private static KeyDataV2Dto copyOf(KeyDataV2Dto source) {
        KeyDataV2Dto copy;
        if (source instanceof SecretKeyDataV2Dto) {
            copy = validSecretKeyData();
        } else if (source instanceof PrivateKeyDataV2Dto) {
            copy = validPrivateKeyData();
        } else {
            copy = validPublicKeyData();
        }
        copy.setMetadata(source.getMetadata());
        return copy;
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got "
                        + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
    }

    private record InvalidKeyData(KeyDataV2Dto keyData, String path, String message) {
    }

    private record MutatedType(KeyDataV2Dto descriptor, KeyTypeV2 fixedType, KeyTypeV2 illegalType) {
    }

    private record PqcAlgorithm(String generatorName, KeyAlgorithm declaredAlgorithm) {
    }
}

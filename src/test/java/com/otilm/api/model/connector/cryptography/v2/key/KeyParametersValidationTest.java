package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.spec.ECGenParameterSpec;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.ecIdentifier;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.madeUp;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class KeyParametersValidationTest {

    private static final Provider BOUNCY_CASTLE = new BouncyCastleProvider();
    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    private static final String LENGTH_REQUIRED = "key length is required";
    private static final String PAIR_IDENTIFIER = "algorithmIdentifier must equal the AlgorithmIdentifier of "
            + "publicKeySpki";

    private static final AlgorithmIdentifier RSA_ENCRYPTION = new AlgorithmIdentifier(
            PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    private static final AlgorithmIdentifier P256 = ecIdentifier(new X962Parameters(SECObjectIdentifiers.secp256r1));
    private static final AlgorithmIdentifier ML_DSA_65 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65);

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest
    @EnumSource(value = KeyAlgorithm.class, names = "AES", mode = EnumSource.Mode.EXCLUDE)
    void acceptsAPrivateKeyWithOrWithoutItsLength(KeyAlgorithm algorithm) {
        // given
        PrivateKeyDataV2Dto withLength = privateKey(algorithm, 256, null);
        PrivateKeyDataV2Dto withoutLength = privateKey(algorithm, null, null);

        // when
        Set<ConstraintViolation<PrivateKeyDataV2Dto>> withLengthViolations = VALIDATOR.validate(withLength);
        Set<ConstraintViolation<PrivateKeyDataV2Dto>> withoutLengthViolations = VALIDATOR.validate(withoutLength);

        // then
        assertNoViolations(withLengthViolations);
        assertNoViolations(withoutLengthViolations);
    }

    @ParameterizedTest
    @EnumSource(value = KeyAlgorithm.class, names = {"AES", "UNKNOWN"})
    void rejectsASecretKeyWithoutItsLength(KeyAlgorithm algorithm) {
        // given
        SecretKeyDataV2Dto key = secretKey(algorithm, null);

        // when
        Set<ConstraintViolation<SecretKeyDataV2Dto>> violations = VALIDATOR.validate(key);

        // then
        assertHasViolation(violations, "length", LENGTH_REQUIRED);
    }

    @ParameterizedTest
    @ValueSource(ints = {64, 128, 255, 512})
    void acceptsAnAesSecretKeyOfAnyPositiveLength(int length) {
        // given
        SecretKeyDataV2Dto key = secretKey(KeyAlgorithm.AES, length);

        // when
        Set<ConstraintViolation<SecretKeyDataV2Dto>> violations = VALIDATOR.validate(key);

        // then
        assertNoViolations(violations);
    }

    @Test
    void acceptsThePublicDescriptorOfAConnectorReleasedBefore2_21() throws Exception {
        // given
        String releasedConnectorDescriptor = publicDescriptorWithLength();

        // when
        KeyDataV2Dto key = MAPPER.readValue(releasedConnectorDescriptor, KeyDataV2Dto.class);

        // then
        assertNoViolations(VALIDATOR.validate(key));
    }

    @Test
    void writesThePublicDescriptorWithoutTheLengthItRead() throws Exception {
        // given
        KeyDataV2Dto key = MAPPER.readValue(publicDescriptorWithLength(), KeyDataV2Dto.class);

        // when
        String written = MAPPER.writeValueAsString(key);

        // then
        assertFalse(written.contains("length"), written);
    }

    @Test
    void carriesThePrivateIdentifierAsBase64DerAndKeepsItOutOfToString() throws Exception {
        // given
        byte[] identifier = der(P256);
        String json = """
                {"type":"Private","algorithm":"ECDSA","algorithmIdentifier":"%s"}
                """.formatted(Base64.getEncoder().encodeToString(identifier));

        // when
        PrivateKeyDataV2Dto key = (PrivateKeyDataV2Dto) MAPPER.readValue(json, KeyDataV2Dto.class);
        String written = MAPPER.writeValueAsString(key);

        // then
        assertArrayEquals(identifier, key.getAlgorithmIdentifier());
        assertTrue(written.contains("\"algorithmIdentifier\":\"" + Base64.getEncoder().encodeToString(identifier)),
                written);
        assertFalse(key.toString().contains(Arrays.toString(identifier)));
        assertNoViolations(VALIDATOR.validate(key));
    }

    @Test
    void copiesThePrivateIdentifierInAndOut() throws Exception {
        // given
        byte[] callerOwned = der(P256);
        PrivateKeyDataV2Dto key = privateKey(KeyAlgorithm.ECDSA, null, null);
        key.setAlgorithmIdentifier(callerOwned);

        // when
        callerOwned[0] = 0;
        byte[] read = key.getAlgorithmIdentifier();
        read[1] = 0;

        // then
        assertArrayEquals(der(P256), key.getAlgorithmIdentifier());
    }

    @Test
    void rejectsAPrivateIdentifierOtherThanCanonicalDer() {
        // given
        PrivateKeyDataV2Dto key = privateKey(KeyAlgorithm.ECDSA, null, null);
        byte[] notDer = {1, 2, 3};

        // when
        IllegalArgumentException rejected = assertThrows(IllegalArgumentException.class,
                () -> key.setAlgorithmIdentifier(notDer));

        // then
        assertEquals("algorithmIdentifier is invalid: AlgorithmIdentifier must be canonical DER",
                rejected.getMessage());
    }

    @Test
    void rejectsAnSpkiOnAnExplicitCurvePastTheBoundBeforeParsingTheKey() throws Exception {
        // given
        X9ECParameters longOrder = madeUp(256, 400_000, BigInteger.TWO);
        byte[] spki = new SubjectPublicKeyInfo(ecIdentifier(longOrder), longOrder.getG().getEncoded(false))
                .getEncoded(ASN1Encoding.DER);
        PublicKeyDataV2Dto key = new PublicKeyDataV2Dto();

        // when
        IllegalArgumentException rejected = assertTimeoutPreemptively(Duration.ofMillis(500),
                () -> assertThrows(IllegalArgumentException.class, () -> key.setPublicKeySpki(spki)));

        // then
        assertEquals("publicKeySpki must state an explicit curve within the size of the largest named curve",
                rejected.getMessage());
    }

    @Test
    void acceptsAnSpkiOnAnExplicitCurveWithinTheBound() throws Exception {
        // given
        byte[] named = ecSpki("secp256r1");
        SubjectPublicKeyInfo parsed = SubjectPublicKeyInfo.getInstance(named);
        byte[] explicit = new SubjectPublicKeyInfo(ecIdentifier(ECNamedCurveTable.getByName("secp256r1")),
                parsed.getPublicKeyData().getBytes()).getEncoded(ASN1Encoding.DER);
        PublicKeyDataV2Dto key = publicKey(KeyAlgorithm.ECDSA, explicit);

        // when
        Set<ConstraintViolation<PublicKeyDataV2Dto>> violations = VALIDATOR.validate(key);

        // then
        assertNoViolations(violations);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pairEnvelopes")
    void acceptsAPairWhosePrivateIdentifierNamesTheSpkiParameters(PairEnvelope envelope) throws Exception {
        // given
        Object rsa = envelope
                .build(publicKey(KeyAlgorithm.RSA, rsaSpki(2048)), privateKey(KeyAlgorithm.RSA, 2048, RSA_ENCRYPTION));
        Object ecNamed = envelope
                .build(publicKey(KeyAlgorithm.ECDSA, ecSpki("secp256r1")), privateKey(KeyAlgorithm.ECDSA, null, P256));
        Object mlDsa = envelope
                .build(publicKey(KeyAlgorithm.MLDSA, bcSpki("ML-DSA-65")),
                        privateKey(KeyAlgorithm.MLDSA, null, ML_DSA_65));
        Object withoutIdentifier = envelope
                .build(publicKey(KeyAlgorithm.MLDSA, bcSpki("ML-DSA-65")), privateKey(KeyAlgorithm.MLDSA, null, null));

        // when
        List<Set<ConstraintViolation<Object>>> violations = Stream
                .of(rsa, ecNamed, mlDsa, withoutIdentifier)
                .map(VALIDATOR::validate)
                .toList();

        // then
        violations.forEach(KeyParametersValidationTest::assertNoViolations);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pairEnvelopes")
    void rejectsAPairWhosePrivateIdentifierNamesOtherParameters(PairEnvelope envelope) throws Exception {
        // given
        Object otherCurve = envelope
                .build(publicKey(KeyAlgorithm.ECDSA, ecSpki("secp384r1")), privateKey(KeyAlgorithm.ECDSA, null, P256));
        Object otherParameterSet = envelope
                .build(publicKey(KeyAlgorithm.MLDSA, bcSpki("ML-DSA-44")),
                        privateKey(KeyAlgorithm.MLDSA, null, ML_DSA_65));
        Object pssBesideRsaEncryption = envelope
                .build(publicKey(KeyAlgorithm.RSA, rsaSpki(2048)), privateKey(KeyAlgorithm.RSA, 2048,
                        new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS)));
        Object explicitBesideNamedCurve = envelope
                .build(publicKey(KeyAlgorithm.ECDSA, ecSpki("secp256r1")),
                        privateKey(KeyAlgorithm.ECDSA, null, ecIdentifier(ECNamedCurveTable.getByName("secp256r1"))));

        // when
        List<Set<ConstraintViolation<Object>>> violations = Stream
                .of(otherCurve, otherParameterSet, pssBesideRsaEncryption, explicitBesideNamedCurve)
                .map(VALIDATOR::validate)
                .toList();

        // then
        violations.forEach(found -> assertHasViolation(found, "keyParametersMatching", PAIR_IDENTIFIER));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pairEnvelopes")
    void acceptsAPairWhosePrivateLengthDiffersFromTheRsaModulus(PairEnvelope envelope) throws Exception {
        // given
        Object declaredRsa = envelope
                .build(publicKey(KeyAlgorithm.RSA, rsaSpki(2048)), privateKey(KeyAlgorithm.RSA, 3072, null));
        Object declaredUnknown = envelope
                .build(publicKey(KeyAlgorithm.UNKNOWN, rsaSpki(2048)), privateKey(KeyAlgorithm.UNKNOWN, 1024, null));

        // when
        List<Set<ConstraintViolation<Object>>> violations = Stream
                .of(declaredRsa, declaredUnknown)
                .map(VALIDATOR::validate)
                .toList();

        // then
        violations.forEach(KeyParametersValidationTest::assertNoViolations);
    }

    @Test
    void describesAListedPrivateKeyWithoutItsPublicHalfOnItsOwn() {
        // given
        ListedKeyPairV2Dto pair = listedKeyPair(null, privateKey(KeyAlgorithm.ECDSA, null, P256));

        // when
        Set<ConstraintViolation<ListedKeyPairV2Dto>> violations = VALIDATOR.validate(pair);

        // then
        assertNoViolations(violations);
    }

    static Stream<Named<PairEnvelope>> pairEnvelopes() {
        return Stream
                .of(named("key-pair response", KeyParametersValidationTest::keyPair),
                        named("listed key pair", KeyParametersValidationTest::listedKeyPair));
    }

    @FunctionalInterface
    interface PairEnvelope {

        Object build(PublicKeyDataV2Dto publicKey, PrivateKeyDataV2Dto privateKey);
    }

    private static PrivateKeyDataV2Dto privateKey(KeyAlgorithm algorithm, Integer length,
            AlgorithmIdentifier identifier) {
        PrivateKeyDataV2Dto key = new PrivateKeyDataV2Dto();
        key.setAlgorithm(algorithm);
        key.setLength(length);
        if (identifier != null) {
            key.setAlgorithmIdentifier(der(identifier));
        }
        return key;
    }

    private static SecretKeyDataV2Dto secretKey(KeyAlgorithm algorithm, Integer length) {
        SecretKeyDataV2Dto key = new SecretKeyDataV2Dto();
        key.setAlgorithm(algorithm);
        key.setLength(length);
        return key;
    }

    private static PublicKeyDataV2Dto publicKey(KeyAlgorithm algorithm, byte[] spki) {
        PublicKeyDataV2Dto key = new PublicKeyDataV2Dto();
        key.setAlgorithm(algorithm);
        key.setPublicKeySpki(spki);
        return key;
    }

    private static KeyPairDataResponseV2Dto keyPair(PublicKeyDataV2Dto publicKey, PrivateKeyDataV2Dto privateKey) {
        KeyPairDataResponseV2Dto pair = new KeyPairDataResponseV2Dto();
        pair.setPublicKeyData(publicKeyResponse(publicKey));
        pair.setPrivateKeyData(privateKeyResponse(privateKey));
        pair.setKeyPairMeta(validMetadata());
        return pair;
    }

    private static ListedKeyPairV2Dto listedKeyPair(PublicKeyDataV2Dto publicKey, PrivateKeyDataV2Dto privateKey) {
        ListedKeyPairV2Dto pair = new ListedKeyPairV2Dto();
        pair.setPublicKeyData(publicKey == null ? null : publicKeyResponse(publicKey));
        pair.setPrivateKeyData(privateKeyResponse(privateKey));
        pair.setKeyPairMeta(validMetadata());
        return pair;
    }

    private static PublicKeyDataResponseV2Dto publicKeyResponse(PublicKeyDataV2Dto keyData) {
        PublicKeyDataResponseV2Dto response = new PublicKeyDataResponseV2Dto();
        response.setKeyMeta(validMetadata());
        response.setKeyData(keyData);
        return response;
    }

    private static PrivateKeyDataResponseV2Dto privateKeyResponse(PrivateKeyDataV2Dto keyData) {
        PrivateKeyDataResponseV2Dto response = new PrivateKeyDataResponseV2Dto();
        response.setKeyMeta(validMetadata());
        response.setKeyData(keyData);
        return response;
    }

    private static String publicDescriptorWithLength() {
        return """
                {"type":"Public","algorithm":"RSA","length":2048,"publicKeySpki":"%s"}
                """.formatted(Base64.getEncoder().encodeToString(rsaSpki(2048)));
    }

    private static byte[] rsaSpki(int bits) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(bits);
            return generator.generateKeyPair().getPublic().getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] ecSpki(String curve) throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec(curve));
        return generator.generateKeyPair().getPublic().getEncoded();
    }

    private static byte[] bcSpki(String algorithm) throws GeneralSecurityException {
        return KeyPairGenerator.getInstance(algorithm, BOUNCY_CASTLE).generateKeyPair().getPublic().getEncoded();
    }

    private static byte[] der(AlgorithmIdentifier identifier) {
        try {
            return identifier.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertNoViolations(Set<? extends ConstraintViolation<?>> violations) {
        assertTrue(violations.isEmpty(), () -> "Expected no violations, got " + describe(violations));
    }

    private static List<String> describe(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got " + describe(violations));
    }
}

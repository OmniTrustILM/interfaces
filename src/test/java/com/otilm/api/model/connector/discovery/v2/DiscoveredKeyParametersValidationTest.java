package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

/** A discovered key names its parameters the way a v2 key descriptor does. */
class DiscoveredKeyParametersValidationTest {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    private static final String IDENTIFIER_PATH = "algorithmIdentifier";
    private static final String NOT_BASE64 = "algorithmIdentifier must be base64-encoded";
    private static final String INVALID = "algorithmIdentifier is invalid: ";
    private static final String MALFORMED = INVALID + "AlgorithmIdentifier must be canonical DER";
    private static final String BESIDE_AN_SPKI = "algorithmIdentifier must be absent beside a SubjectPublicKeyInfo "
            + "publicKey";
    private static final String ON_A_SECRET_KEY = "algorithmIdentifier must be absent for a secret key";
    private static final String FORMAT_OF_PARAMETERS = "publicKeyFormat must be a public-key format";

    private static final String SPKI = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A";
    private static final String P256 = base64(
            new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp256r1));
    private static final String ML_DSA_65 = base64(new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65));

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("keysNamedByTheirIdentifier")
    void acceptsAKeyWithoutAnSpkiNamedByItsIdentifier(DiscoveredKeyDto key) {
        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertEquals(Set.of(), messages(violations));
    }

    static Stream<Named<DiscoveredKeyDto>> keysNamedByTheirIdentifier() {
        DiscoveredKeyDto rawPublicKey = key(KeyType.PUBLIC_KEY, KeyAlgorithm.ECDSA, null, P256);
        rawPublicKey.setPublicKeyFormat(KeyFormat.RAW);
        rawPublicKey.setPublicKey("BAEC");
        return Stream
                .of(named("private EC key", key(KeyType.PRIVATE_KEY, KeyAlgorithm.ECDSA, null, P256)),
                        named("split ML-DSA key", key(KeyType.SPLIT_KEY, KeyAlgorithm.MLDSA, null, ML_DSA_65)),
                        named("private RSA key with its length",
                                key(KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, 2048,
                                        base64(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption,
                                                DERNull.INSTANCE)))),
                        named("public EC key in raw format", rawPublicKey),
                        named("unlisted OID", key(KeyType.PRIVATE_KEY, KeyAlgorithm.MLDSA, null,
                                base64(new AlgorithmIdentifier(new ASN1ObjectIdentifier("2.16.840.1.101.3.4.3.99"))))));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("identifiersBreakingARule")
    void rejectsAnIdentifierThatBreaksARule(String identifier, String rule) {
        // given
        DiscoveredKeyDto key = key(KeyType.PRIVATE_KEY, KeyAlgorithm.ECDSA, null, identifier);

        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertEquals(Set.of(IDENTIFIER_PATH + ": " + rule), messages(violations));
    }

    static Stream<Arguments> identifiersBreakingARule() {
        return Stream
                .of(Arguments.of(named("not base64", "not base64!"), NOT_BASE64),
                        Arguments.of(named("not DER", "AQID"), MALFORMED));
    }

    @ParameterizedTest(name = "publicKeyFormat {0}")
    @NullSource
    @EnumSource(value = KeyFormat.class, names = {"SPKI"})
    void rejectsAnIdentifierBesideAnSpki(KeyFormat format) {
        // given
        DiscoveredKeyDto key = key(KeyType.PUBLIC_KEY, KeyAlgorithm.ECDSA, null, P256);
        key.setPublicKeyFormat(format);
        key.setPublicKey(SPKI);

        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertEquals(Set.of(IDENTIFIER_PATH + ": " + BESIDE_AN_SPKI), messages(violations));
    }

    @Test
    void rejectsAnIdentifierOnASecretKey() {
        // given
        DiscoveredKeyDto key = key(KeyType.SECRET_KEY, KeyAlgorithm.AES, 256, P256);

        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertEquals(Set.of(IDENTIFIER_PATH + ": " + ON_A_SECRET_KEY), messages(violations));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("anyLengths")
    void leavesTheLengthUnchecked(DiscoveredKeyDto key) {
        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertEquals(Set.of(), messages(violations));
    }

    static Stream<Named<DiscoveredKeyDto>> anyLengths() {
        DiscoveredKeyDto rsaBesideAnSpki = key(KeyType.PUBLIC_KEY, KeyAlgorithm.RSA, 1, null);
        rsaBesideAnSpki.setPublicKey(SPKI);
        return Stream
                .of(named("private RSA key without its length", key(KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, null, null)),
                        named("private RSA key of length 0", key(KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, 0, null)),
                        named("private RSA key of a negative length",
                                key(KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, -1, null)),
                        named("private EC key with a length", key(KeyType.PRIVATE_KEY, KeyAlgorithm.ECDSA, 256, P256)),
                        named("secret AES key without its length",
                                key(KeyType.SECRET_KEY, KeyAlgorithm.AES, null, null)),
                        named("secret AES key of another size", key(KeyType.SECRET_KEY, KeyAlgorithm.AES, 100, null)),
                        named("RSA key stating another length beside an SPKI", rsaBesideAnSpki));
    }

    @Test
    void rejectsAlgorithmIdentifierAsThePublicKeyFormat() {
        // given
        DiscoveredKeyDto key = key(KeyType.PUBLIC_KEY, KeyAlgorithm.ECDSA, null, null);
        key.setPublicKeyFormat(KeyFormat.ALGORITHM_IDENTIFIER);
        key.setPublicKey(P256);

        // when
        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(key);

        // then
        assertTrue(messages(violations).contains("publicKeyFormat: " + FORMAT_OF_PARAMETERS),
                () -> "got " + messages(violations));
    }

    @Test
    void namesTheBrokenRuleWithoutTheItemData() {
        // given
        List<DiscoveredKeyDto> keys = Stream
                .of("bm90IERFUg==", "AQIDBA==")
                .map(identifier -> key(KeyType.PRIVATE_KEY, KeyAlgorithm.ECDSA, null, identifier))
                .toList();

        // when
        List<Set<String>> violations = keys.stream().map(key -> messages(VALIDATOR.validate(key))).toList();

        // then
        assertEquals(List.of(Set.of(IDENTIFIER_PATH + ": " + MALFORMED), Set.of(IDENTIFIER_PATH + ": " + MALFORMED)),
                violations);
    }

    @Test
    void leavesAMalformedIdentifierToValidation() throws Exception {
        // given
        String json = """
                {"resource":"keys","type":"Private","algorithm":"ECDSA","algorithmIdentifier":"not base64!"}
                """;

        // when
        DiscoveredKeyDto key = MAPPER.readValue(json, DiscoveredKeyDto.class);

        // then
        assertEquals("not base64!", key.getAlgorithmIdentifier());
        assertEquals(Set.of(IDENTIFIER_PATH + ": " + NOT_BASE64), messages(VALIDATOR.validate(key)));
    }

    private static DiscoveredKeyDto key(KeyType type, KeyAlgorithm algorithm, Integer length, String identifier) {
        DiscoveredKeyDto key = new DiscoveredKeyDto();
        key.setType(type);
        key.setAlgorithm(algorithm);
        key.setLength(length);
        key.setAlgorithmIdentifier(identifier);
        return key;
    }

    private static String base64(AlgorithmIdentifier identifier) {
        try {
            return Base64.getEncoder().encodeToString(identifier.getEncoded(ASN1Encoding.DER));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Set<String> messages(Set<ConstraintViolation<DiscoveredKeyDto>> violations) {
        return violations
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toSet());
    }
}

package com.otilm.api.model.common.cryptography;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.ecIdentifier;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.explicit;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.explicitReplacing;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.madeUp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class KeyAlgorithmIdentifiersTest {

    private static final Provider BOUNCY_CASTLE = new BouncyCastleProvider();

    private static final String MALFORMED = "AlgorithmIdentifier must be canonical DER";

    private static final AlgorithmIdentifier RSA_ENCRYPTION = new AlgorithmIdentifier(
            PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    private static final ASN1ObjectIdentifier UNLISTED_CURVE = new ASN1ObjectIdentifier("1.2.3.4.5");
    private static final AlgorithmIdentifier SHA256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    private static final AlgorithmIdentifier MGF1_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1,
            SHA256);
    private static final int GENERATOR_INDEX = 3;

    @ParameterizedTest
    @MethodSource("generatedKeys")
    void readsTheIdentifiersOfGeneratedKeys(String generatorAlgorithm, AlgorithmParameterSpec spec) throws Exception {
        // given
        KeyPair keyPair = generate(generatorAlgorithm, spec);
        byte[] publicIdentifier = encoded(
                SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()).getAlgorithm());
        byte[] privateIdentifier = encoded(
                PrivateKeyInfo.getInstance(keyPair.getPrivate().getEncoded()).getPrivateKeyAlgorithm());

        // when
        AlgorithmIdentifier readPublic = KeyAlgorithmIdentifiers.read(publicIdentifier);
        AlgorithmIdentifier readPrivate = KeyAlgorithmIdentifiers.read(privateIdentifier);

        // then
        assertTrue(Arrays.equals(publicIdentifier, encoded(readPublic)));
        assertTrue(Arrays.equals(privateIdentifier, encoded(readPrivate)));
    }

    static Stream<Arguments> generatedKeys() {
        return Stream
                .of(arguments("RSA", null), arguments("RSASSA-PSS", null),
                        arguments("EC", new ECGenParameterSpec("secp256r1")),
                        arguments("EC", new ECGenParameterSpec("secp521r1")),
                        arguments("EC", new ECGenParameterSpec("brainpoolP256r1")), arguments("ML-DSA-65", null),
                        arguments("SLH-DSA-SHA2-128S", null), arguments("ML-KEM-768", null),
                        arguments("FALCON-512", null), arguments("HASH-ML-DSA", null), arguments("HASH-SLH-DSA", null));
    }

    @ParameterizedTest
    @MethodSource("anyParameters")
    void readsAnyOidAndParametersInCanonicalDer(AlgorithmIdentifier identifier) throws Exception {
        // given
        byte[] encoded = encoded(identifier);

        // when
        AlgorithmIdentifier read = KeyAlgorithmIdentifiers.read(encoded);

        // then
        assertEquals(identifier, read);
    }

    static Stream<Named<AlgorithmIdentifier>> anyParameters() {
        byte[] offTheCurve = new byte[65];
        offTheCurve[0] = 4;
        offTheCurve[32] = 1;
        offTheCurve[64] = 1;
        return Stream
                .of(named("RSASSA-PSS with its parameters",
                        pss(new RSASSAPSSparams(SHA256, MGF1_SHA256, new ASN1Integer(32),
                                RSASSAPSSparams.DEFAULT_TRAILER_FIELD))),
                        named("rsaEncryption without NULL",
                                new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption)),
                        named("an explicit curve", ecIdentifier(explicit("secp256r1"))),
                        named("an explicit binary curve", ecIdentifier(explicit("sect571r1"))),
                        named("an explicit curve with a generator off its curve",
                                ecIdentifier(explicitReplacing("secp256r1", GENERATOR_INDEX,
                                        new DEROctetString(offTheCurve)))),
                        named("an explicit curve past the bound", ecIdentifier(madeUp(256, 400_000, BigInteger.TWO))),
                        named("a named curve outside BouncyCastle's table",
                                ecIdentifier(new X962Parameters(UNLISTED_CURVE))),
                        named("ML-DSA with NULL",
                                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65, DERNull.INSTANCE)),
                        named("an unlisted OID without parameters",
                                new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.101.112"))),
                        named("an unlisted OID with parameters",
                                new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.3.4"), new ASN1Integer(7))));
    }

    @ParameterizedTest
    @MethodSource("malformedEncodings")
    void rejectsAnEncodingOtherThanCanonicalDer(byte[] encoded) {
        // when
        IllegalArgumentException rejected = assertThrows(IllegalArgumentException.class,
                () -> KeyAlgorithmIdentifiers.read(encoded));

        // then
        assertEquals(MALFORMED, rejected.getMessage());
    }

    static Stream<Named<byte[]>> malformedEncodings() throws IOException {
        byte[] canonical = encoded(RSA_ENCRYPTION);
        byte[] longFormLength = new byte[canonical.length + 1];
        longFormLength[0] = canonical[0];
        longFormLength[1] = (byte) 0x81;
        System.arraycopy(canonical, 1, longFormLength, 2, canonical.length - 1);
        return Stream
                .of(named("no bytes", new byte[0]), named("bytes outside ASN.1", new byte[]{1, 2, 3}),
                        named("trailing bytes", Arrays.copyOf(canonical, canonical.length + 2)),
                        named("a long-form length", longFormLength),
                        named("an INTEGER", new ASN1Integer(1).getEncoded(ASN1Encoding.DER)),
                        named("a sequence of three",
                                new DERSequence(new ASN1Encodable[]{
                                        PKCSObjectIdentifiers.rsaEncryption,
                                        DERNull.INSTANCE,
                                        DERNull.INSTANCE}).getEncoded(ASN1Encoding.DER)));
    }

    private static KeyPair generate(String algorithm, AlgorithmParameterSpec spec) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, BOUNCY_CASTLE);
        if (spec != null) {
            generator.initialize(spec);
        }
        return generator.generateKeyPair();
    }

    private static AlgorithmIdentifier pss(ASN1Encodable parameters) {
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, parameters);
    }

    private static byte[] encoded(AlgorithmIdentifier identifier) throws IOException {
        return identifier.getEncoded(ASN1Encoding.DER);
    }
}

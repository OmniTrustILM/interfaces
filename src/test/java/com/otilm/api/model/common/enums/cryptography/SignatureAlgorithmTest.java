package com.otilm.api.model.common.enums.cryptography;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

/**
 * Pins the wire identity of the post-quantum signature algorithms against the identifiers FIPS 204 and FIPS 205 assign.
 */
class SignatureAlgorithmTest {

    private static final String SHA_256_OID = "2.16.840.1.101.3.4.2.1";
    private static final String SHA_512_OID = "2.16.840.1.101.3.4.2.3";

    static Stream<Arguments> postQuantumParameterSets() {
        return Stream
                .of(Arguments.of(SignatureAlgorithm.ML_DSA_44, "ML-DSA-44", "2.16.840.1.101.3.4.3.17", SHA_512_OID),
                        Arguments.of(SignatureAlgorithm.ML_DSA_65, "ML-DSA-65", "2.16.840.1.101.3.4.3.18", SHA_512_OID),
                        Arguments.of(SignatureAlgorithm.ML_DSA_87, "ML-DSA-87", "2.16.840.1.101.3.4.3.19", SHA_512_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_128S, "SLH-DSA-SHA2-128S",
                                        "2.16.840.1.101.3.4.3.20", SHA_256_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_128F, "SLH-DSA-SHA2-128F",
                                        "2.16.840.1.101.3.4.3.21", SHA_256_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_192S, "SLH-DSA-SHA2-192S",
                                        "2.16.840.1.101.3.4.3.22", SHA_512_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_192F, "SLH-DSA-SHA2-192F",
                                        "2.16.840.1.101.3.4.3.23", SHA_512_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_256S, "SLH-DSA-SHA2-256S",
                                        "2.16.840.1.101.3.4.3.24", SHA_512_OID),
                        Arguments
                                .of(SignatureAlgorithm.SLH_DSA_SHA2_256F, "SLH-DSA-SHA2-256F",
                                        "2.16.840.1.101.3.4.3.25", SHA_512_OID));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("postQuantumParameterSets")
    void carriesTheIdentifiersItsParameterSetIsStandardizedUnder(SignatureAlgorithm algorithm, String expectedCode,
            String expectedAlgorithmOid, String expectedDigestOid) {
        assertEquals(expectedCode, algorithm.getCode());
        assertEquals(expectedAlgorithmOid, algorithm.getAlgorithmIdentifier().getAlgorithm().getId());
        assertEquals(expectedDigestOid, algorithm.getDigestAlgorithmIdentifier().getAlgorithm().getId());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("postQuantumParameterSets")
    void doesNotSpellItsDigestInItsCode(SignatureAlgorithm algorithm, String expectedCode, String expectedAlgorithmOid,
            String expectedDigestOid) {
        assertTrue(algorithm.isDigestAlgorithmIsImplicit(), expectedCode + " must declare its digest implicit");
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("postQuantumParameterSets")
    void omitsAlgorithmParametersEntirely(SignatureAlgorithm algorithm, String expectedCode,
            String expectedAlgorithmOid, String expectedDigestOid) {
        assertNull(algorithm.getAlgorithmIdentifier().getParameters(),
                expectedCode + " must encode absent parameters, not NULL");
    }

    @ParameterizedTest
    @EnumSource(SignatureAlgorithm.class)
    void findsAnyAlgorithmByItsCodeWhateverTheCase(SignatureAlgorithm algorithm) {
        assertEquals(algorithm, SignatureAlgorithm.findByCode(algorithm.getCode()));
        assertEquals(algorithm, SignatureAlgorithm.findByCode(algorithm.getCode().toUpperCase(Locale.ROOT)));
        assertEquals(algorithm, SignatureAlgorithm.findByCode(algorithm.getCode().toLowerCase(Locale.ROOT)));
    }

    @Test
    void namesEveryAlgorithmWithADistinctCode() {
        assertEquals(SignatureAlgorithm.values().length,
                Stream.of(SignatureAlgorithm.values()).map(SignatureAlgorithm::getCode).distinct().count());
    }

    @Test
    void namesEveryAlgorithmWithADistinctIdentifier() throws IOException {
        List<String> encoded = new ArrayList<>();
        for (SignatureAlgorithm algorithm : SignatureAlgorithm.values()) {
            encoded.add(Hex.toHexString(algorithm.getAlgorithmIdentifier().getEncoded()));
        }

        assertEquals(encoded.size(), Set.copyOf(encoded).size(), "two algorithms encode the same identifier");
    }

    @ParameterizedTest
    @EnumSource(SignatureAlgorithm.class)
    void isIdentifiedByItsOwnIdentifier(SignatureAlgorithm algorithm) {
        assertTrue(algorithm.isIdentifiedBy(algorithm.getAlgorithmIdentifier()));
    }

    @ParameterizedTest
    @EnumSource(value = SignatureAlgorithm.class, names = ".*_WITH_RSA", mode = EnumSource.Mode.MATCH_ALL)
    void isIdentifiedByAnRsaIdentifierWithItsParametersLeftAbsent(SignatureAlgorithm algorithm) {
        assertTrue(
                algorithm.isIdentifiedBy(new AlgorithmIdentifier(algorithm.getAlgorithmIdentifier().getAlgorithm())));
    }

    @Test
    void isIdentifiedByAPssIdentifierWithItsHashParametersLeftAbsent() {
        AlgorithmIdentifier sha256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);

        assertTrue(SignatureAlgorithm.SHA256_WITH_RSA_PSS
                .isIdentifiedBy(pss(sha256, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, sha256), 32)));
    }

    @ParameterizedTest
    @MethodSource("anotherSha256PssScheme")
    void isNotIdentifiedByAnotherPssScheme(AlgorithmIdentifier identifier) {
        assertFalse(SignatureAlgorithm.SHA256_WITH_RSA_PSS.isIdentifiedBy(identifier));
    }

    static Stream<Named<AlgorithmIdentifier>> anotherSha256PssScheme() {
        AlgorithmIdentifier sha256 = hash(NISTObjectIdentifiers.id_sha256);
        AlgorithmIdentifier sha384 = hash(NISTObjectIdentifiers.id_sha384);
        AlgorithmIdentifier mgf1Sha256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, sha256);
        return Stream
                .of(named("another salt length", pss(sha256, mgf1Sha256, 20)),
                        named("another hash", pss(sha384, mgf1Sha256, 32)),
                        named("another mask hash",
                                pss(sha256, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, sha384), 32)),
                        named("another mask function",
                                pss(sha256, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, sha256), 32)),
                        named("another trailer",
                                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                                        new RSASSAPSSparams(sha256, mgf1Sha256, new ASN1Integer(32),
                                                new ASN1Integer(2)))),
                        named("malformed parameters",
                                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                                        new DEROctetString(new byte[]{1}))),
                        named("absent parameters", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS)));
    }

    @ParameterizedTest
    @EnumSource(value = SignatureAlgorithm.class, names = {"SHA256_WITH_ECDSA", "ED25519", "ML_DSA_44"})
    void isNotIdentifiedByNullParametersItsStandardLeavesAbsent(SignatureAlgorithm algorithm) {
        assertFalse(algorithm
                .isIdentifiedBy(
                        new AlgorithmIdentifier(algorithm.getAlgorithmIdentifier().getAlgorithm(), DERNull.INSTANCE)));
    }

    @Test
    void isNotIdentifiedByAnotherAlgorithm() {
        assertFalse(SignatureAlgorithm.SHA256_WITH_RSA
                .isIdentifiedBy(SignatureAlgorithm.SHA384_WITH_RSA.getAlgorithmIdentifier()));
    }

    private static AlgorithmIdentifier hash(ASN1ObjectIdentifier oid) {
        return new AlgorithmIdentifier(oid, DERNull.INSTANCE);
    }

    private static AlgorithmIdentifier pss(AlgorithmIdentifier hash, AlgorithmIdentifier maskGeneration,
            int saltLength) {
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, new RSASSAPSSparams(hash, maskGeneration,
                new ASN1Integer(saltLength), RSASSAPSSparams.DEFAULT_TRAILER_FIELD));
    }
}

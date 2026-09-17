package com.otilm.api.model.common.enums.cryptography;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}

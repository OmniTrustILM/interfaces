package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The RSA-PSS parameters an algorithm code fixes are otherwise held only in the platform's own algorithm identifiers,
 * and a connector generated from the published document reads none of those, so a signer left to choose them picks its
 * library's defaults and produces signatures that contradict the parameters the platform declares.
 */
class RsaPssParametersSchemaTest {

    private static final String RSA_PSS_PARAMETERS = "RSA-PSS codes use MGF1 with the same hash as the code, a salt as "
            + "long as that hash's output, and trailer field 1.";

    static Stream<Class<?>> signatureRequests() {
        return Stream.of(SignDataRequestV2Dto.class, VerifyDataRequestV2Dto.class);
    }

    @ParameterizedTest
    @MethodSource("signatureRequests")
    void signatureAttributesStateTheRsaPssParameters(Class<?> request) throws NoSuchFieldException {
        // given
        Schema schema = request.getDeclaredField("signatureAttributes").getAnnotation(Schema.class);

        // then
        assertNotNull(schema);
        String description = schema.description();
        assertTrue(description.contains(RSA_PSS_PARAMETERS),
                request.getSimpleName() + ".signatureAttributes must state the RSA-PSS parameters");
        assertNoJargon(request.getSimpleName() + ".signatureAttributes", description);
        assertLanguageNeutral(request.getSimpleName() + ".signatureAttributes", description);
    }

    @Test
    void everyRsaPssCodeDeclaresTheParametersTheDocumentStates() throws NoSuchAlgorithmException {
        // given
        List<SignatureAlgorithm> rsaPss = Arrays
                .stream(SignatureAlgorithm.values())
                .filter(algorithm -> PKCSObjectIdentifiers.id_RSASSA_PSS
                        .equals(algorithm.getAlgorithmIdentifier().getAlgorithm()))
                .toList();

        // then
        assertFalse(rsaPss.isEmpty());
        for (SignatureAlgorithm algorithm : rsaPss) {
            RSASSAPSSparams parameters = RSASSAPSSparams
                    .getInstance(algorithm.getAlgorithmIdentifier().getParameters());
            ASN1ObjectIdentifier hash = parameters.getHashAlgorithm().getAlgorithm();
            AlgorithmIdentifier maskGeneration = parameters.getMaskGenAlgorithm();

            assertEquals(PKCSObjectIdentifiers.id_mgf1, maskGeneration.getAlgorithm(), algorithm.getCode());
            assertEquals(hash, AlgorithmIdentifier.getInstance(maskGeneration.getParameters()).getAlgorithm(),
                    algorithm.getCode());
            assertEquals(MessageDigest.getInstance(hash.getId()).getDigestLength(),
                    parameters.getSaltLength().intValueExact(), algorithm.getCode());
            assertEquals(1, parameters.getTrailerField().intValueExact(), algorithm.getCode());
        }
    }
}

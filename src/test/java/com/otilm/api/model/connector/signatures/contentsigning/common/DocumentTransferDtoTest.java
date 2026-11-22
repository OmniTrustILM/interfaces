package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The inline-or-digest rule is what keeps a detached customer document out of the platform entirely.
 */
class DocumentTransferDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    /** The digest arm rejects a length its algorithm could not have produced. */
    private static final byte[] SHA256_DIGEST = digestOfLength(32);

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void eachArmCarriesItsOwnTransferMode() {
        assertEquals(DocumentTransferMode.INLINE, new InlineDocumentTransferDto(new byte[]{1, 2, 3}).getTransferMode());
        assertEquals(DocumentTransferMode.DIGEST_ONLY,
                new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256).getTransferMode());
    }

    @Test
    void anInlineTransferIsValid() {
        assertTrue(VALIDATOR.validate(new InlineDocumentTransferDto(new byte[]{1, 2, 3})).isEmpty());
    }

    @Test
    void aDigestOnlyTransferIsValid() {
        assertTrue(VALIDATOR
                .validate(new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256))
                .isEmpty());
    }

    @Test
    void aDigestWhoseLengthContradictsItsAlgorithmIsRejected() {
        Set<ConstraintViolation<DigestOnlyDocumentTransferDto>> violations = VALIDATOR
                .validate(new DigestOnlyDocumentTransferDto(new byte[]{4, 5, 6}, DigestAlgorithm.SHA_256));

        assertTrue(
                violations
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("digestLengthConsistentWithAlgorithm")),
                "expected a violation on the digest-length rule, got " + paths(violations));
    }

    @Test
    void aDigestMatchingItsAlgorithmsOwnLengthIsAccepted() {
        for (DigestAlgorithm algorithm : DigestAlgorithm.values()) {
            byte[] digest = digestOfLength(algorithm.getDigestSizeBytes());

            assertTrue(VALIDATOR.validate(new DigestOnlyDocumentTransferDto(digest, algorithm)).isEmpty(),
                    algorithm.getCode());
        }
    }

    @Test
    void anInlineTransferWithoutItsDocumentIsRejected() {
        Set<ConstraintViolation<InlineDocumentTransferDto>> violations = VALIDATOR
                .validate(new InlineDocumentTransferDto(null));

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("document")),
                "expected a violation on document, got " + paths(violations));
    }

    /** A digest cannot be interpreted without the algorithm that produced it, so the arm requires both. */
    @Test
    void aDigestTransferWithoutItsAlgorithmIsRejected() {
        Set<ConstraintViolation<DigestOnlyDocumentTransferDto>> violations = VALIDATOR
                .validate(new DigestOnlyDocumentTransferDto(SHA256_DIGEST, null));

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("digestAlgorithm")),
                "expected a violation on digestAlgorithm, got " + paths(violations));
    }

    @Test
    void aDigestTransferWithoutItsDigestIsRejected() {
        Set<ConstraintViolation<DigestOnlyDocumentTransferDto>> violations = VALIDATOR
                .validate(new DigestOnlyDocumentTransferDto(null, DigestAlgorithm.SHA_256));

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("documentDigest")),
                "expected a violation on documentDigest, got " + paths(violations));
    }

    @Test
    void eachArmPublishesItsDiscriminatorOnTheWire() throws Exception {
        assertTrue(mapper
                .writeValueAsString(new InlineDocumentTransferDto(new byte[]{1, 2, 3}))
                .contains("\"transferMode\":\"inline\""));
        assertTrue(mapper
                .writeValueAsString(new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256))
                .contains("\"transferMode\":\"digestOnly\""));
    }

    @Test
    void anInlineTransferRoundTripsThroughTheBaseType() throws Exception {
        InlineDocumentTransferDto original = new InlineDocumentTransferDto(new byte[]{9, 8, 7});

        DocumentTransferDto deserialized = mapper
                .readValue(mapper.writeValueAsString(original), DocumentTransferDto.class);

        InlineDocumentTransferDto inline = assertInstanceOf(InlineDocumentTransferDto.class, deserialized);
        assertArrayEquals(original.document(), inline.document());
    }

    @Test
    void aDigestOnlyTransferRoundTripsThroughTheBaseType() throws Exception {
        DigestOnlyDocumentTransferDto original = new DigestOnlyDocumentTransferDto(SHA256_DIGEST,
                DigestAlgorithm.SHA_256);

        DocumentTransferDto deserialized = mapper
                .readValue(mapper.writeValueAsString(original), DocumentTransferDto.class);

        DigestOnlyDocumentTransferDto digestOnly = assertInstanceOf(DigestOnlyDocumentTransferDto.class, deserialized);
        assertArrayEquals(original.documentDigest(), digestOnly.documentDigest());
        assertEquals(DigestAlgorithm.SHA_256, digestOnly.digestAlgorithm());
    }

    /**
     * Nothing else names the transport, so a body that omits it cannot be bound. Deducing the arm from the properties
     * present would bind this body instead, which is why the discriminator is required rather than inferred.
     */
    @Test
    void aBodyWithoutItsTransferModeIsRejected() {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue("{\"document\":\"AQID\"}", DocumentTransferDto.class));
    }

    @Test
    void aBodyNamingAnUnknownTransportIsRejected() {
        assertThrows(InvalidTypeIdException.class, () -> mapper
                .readValue("{\"transferMode\":\"telepathy\",\"document\":\"AQID\"}", DocumentTransferDto.class));
    }

    /**
     * The discriminator settles the transport, so the digest is never read alongside the document: the arm that binds
     * has nowhere to put it.
     */
    @Test
    void aBodyCarryingBothTransportsCannotBindToBoth() throws Exception {
        String bothTransports = "{\"transferMode\":\"digestOnly\",\"documentDigest\":\"BAUG\","
                + "\"digestAlgorithm\":\"SHA-256\",\"document\":\"AQID\"}";

        assertThrows(UnrecognizedPropertyException.class,
                () -> mapper.readValue(bothTransports, DocumentTransferDto.class),
                "a strict mapper must refuse the document alongside a digest transfer");

        ObjectMapper lenient = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        DocumentTransferDto bound = lenient.readValue(bothTransports, DocumentTransferDto.class);

        assertInstanceOf(DigestOnlyDocumentTransferDto.class, bound,
                "a mapper that tolerates unknown properties must still honour the declared transport");
    }

    /** Customer content must not reach a log line by way of a routine toString. */
    @Test
    void theInlineArmKeepsItsDocumentOutOfToString() {
        String rendered = new InlineDocumentTransferDto(new byte[]{1, 2, 3}).toString();

        assertFalse(rendered.contains("[1, 2, 3]"), "toString leaked the document: " + rendered);
        assertTrue(rendered.contains("3 bytes"), rendered);
    }

    /** A digest is not customer content, and a bare array address would make a log line useless. */
    @Test
    void theDigestArmKeepsItsDigestLegibleInToString() {
        String rendered = new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256).toString();

        assertTrue(rendered.contains("documentDigest=" + Arrays.toString(SHA256_DIGEST)), rendered);
        assertTrue(rendered.contains("SHA_256"), rendered);
    }

    /**
     * A record over a byte[] compares it by reference unless told otherwise, which would surprise every caller. SHA-256
     * and SHA3-256 share a digest length, so only the algorithm distinguishes that pair.
     */
    @Test
    void armsCompareTheirContentRatherThanTheirArrays() {
        assertEquals(new InlineDocumentTransferDto(new byte[]{1, 2, 3}),
                new InlineDocumentTransferDto(new byte[]{1, 2, 3}));
        assertEquals(new InlineDocumentTransferDto(new byte[]{1, 2, 3}).hashCode(),
                new InlineDocumentTransferDto(new byte[]{1, 2, 3}).hashCode());
        assertEquals(new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256),
                new DigestOnlyDocumentTransferDto(SHA256_DIGEST.clone(), DigestAlgorithm.SHA_256));
        assertNotEquals(new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA_256),
                new DigestOnlyDocumentTransferDto(SHA256_DIGEST, DigestAlgorithm.SHA3_256));
    }

    /** The two arms are never interchangeable, however similar the bytes they carry look. */
    @Test
    void armsAreNeverEqualToNullNorToTheOtherArm() {
        InlineDocumentTransferDto inline = new InlineDocumentTransferDto(SHA256_DIGEST);
        DigestOnlyDocumentTransferDto digestOnly = new DigestOnlyDocumentTransferDto(SHA256_DIGEST,
                DigestAlgorithm.SHA_256);

        assertNotEquals(inline, null);
        assertNotEquals(digestOnly, null);
        assertNotEquals(inline, digestOnly);
        assertNotEquals(digestOnly, inline);
        assertNotEquals(inline, new InlineDocumentTransferDto(new byte[]{9, 9, 9}));
    }

    /** An arm may be validated before it is complete, so rendering one must not itself fail. */
    @Test
    void anIncompleteArmStillRenders() {
        assertTrue(new InlineDocumentTransferDto(null).toString().contains("0 bytes"));
        assertTrue(new DigestOnlyDocumentTransferDto(null, null).toString().contains("documentDigest=null"));
    }

    private static <T> String paths(Set<ConstraintViolation<T>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).toList().toString();
    }

    private static byte[] digestOfLength(int length) {
        byte[] digest = new byte[length];
        for (int i = 0; i < length; i++) {
            digest[i] = (byte) (i + 1);
        }
        return digest;
    }
}

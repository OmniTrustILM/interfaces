package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ItemIdentifierFormat;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class SignDocumentRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final byte[] DOCUMENT = "%PDF-1.7".getBytes();
    private static final byte[] SHA_256_DIGEST = new byte[DigestAlgorithm.SHA_256.getDigestSizeBytes()];
    private static final byte[] SHA_384_DIGEST = new byte[DigestAlgorithm.SHA_384.getDigestSizeBytes()];

    @Test
    void accepts_whenEveryItemOfAnInlineRequestCarriesItsDocument() {
        // given
        SignDocumentRequestDto request = inlineRequest(inlineItem("first"), inlineItem("second"));

        // when
        Set<ConstraintViolation<SignDocumentRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @Test
    void accepts_whenEveryItemOfADigestRequestCarriesADigestOfTheNamedAlgorithm() {
        // given
        SignDocumentRequestDto request = digestRequest(digestItem("first"), digestItem("second"));

        // when
        Set<ConstraintViolation<SignDocumentRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @Test
    void accepts_whenADigestRequestCarriesADigestOfALongerNamedAlgorithm() {
        // given
        SignDocumentRequestDto request = digestRequest(DigestAlgorithm.SHA_384, digestItem("first", SHA_384_DIGEST));

        // when
        Set<ConstraintViolation<SignDocumentRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void refusesNamingTheField_whenTheRequestViolatesARule(Invalid invalid) {
        // given
        SignDocumentRequestDto request = invalid.request();

        // when
        Set<ConstraintViolation<SignDocumentRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(
                violations
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals(invalid.path())
                                && v.getMessage().equals(invalid.message())),
                () -> "expected " + invalid.path() + " '" + invalid.message() + "' in " + violations);
    }

    static Stream<Named<Invalid>> invalidRequests() {
        SignDocumentRequestDto noTransferMode = inlineRequest(inlineItem("first"));
        noTransferMode.setTransferMode(null);
        SignDocumentRequestDto inlineWithAlgorithm = inlineRequest(inlineItem("first"));
        inlineWithAlgorithm.setDigestAlgorithm(DigestAlgorithm.SHA_256);
        SignDocumentItemDto inlineWithDigest = inlineItem("second");
        inlineWithDigest.setDocumentDigest(SHA_256_DIGEST);
        SignDocumentRequestDto digestWithoutAlgorithm = digestRequest(digestItem("first"));
        digestWithoutAlgorithm.setDigestAlgorithm(null);
        SignDocumentItemDto digestWithDocument = digestItem("second");
        digestWithDocument.setDocument(DOCUMENT);
        SignDocumentItemDto shortDigest = new SignDocumentItemDto("second", null, new byte[20]);
        SignDocumentRequestDto noItems = inlineRequest();
        noItems.setItems(null);
        SignDocumentRequestDto nullRequestAttribute = inlineRequest(inlineItem("first"));
        nullRequestAttribute.setRequestAttributes(Collections.singletonList(null));
        SignDocumentRequestDto oversizedDigest = digestRequest(DigestAlgorithm.SHA_384,
                digestItem("first", new byte[DigestAlgorithm.SHA_512.getDigestSizeBytes()]));

        return Stream
                .of(invalid("no transfer mode", noTransferMode, "transferMode", "transferMode is required"),
                        invalid("empty items", inlineRequest(), "items", "items must contain at least one item"),
                        invalid("unset items", noItems, "items", "items must contain at least one item"),
                        invalid("null item", inlineRequest(Collections.singletonList(null)), "items[0].<list element>",
                                "items must not contain null items"),
                        invalid("blank identifier", inlineRequest(inlineItem(" ")), "items[0].identifier",
                                "identifier is required"),
                        invalid("repeated identifier", inlineRequest(inlineItem("same"), inlineItem("same")),
                                "items[1].identifier",
                                "identifiers must be unique within the request; duplicates item at index 0"),
                        invalid("identifier over the bound",
                                inlineRequest(inlineItem("x".repeat(ItemIdentifierFormat.MAX_LENGTH + 1))),
                                "items[0].identifier", ItemIdentifierFormat.LENGTH_MESSAGE),
                        invalid("identifier with a control character", inlineRequest(inlineItem("first\r\nforged")),
                                "items[0].identifier", ItemIdentifierFormat.MESSAGE),
                        invalid("inline request naming a digest algorithm", inlineWithAlgorithm, "digestAlgorithm",
                                "digestAlgorithm must be absent when transferMode is inline"),
                        invalid("inline item without its document",
                                inlineRequest(inlineItem("first"), new SignDocumentItemDto("second", null, null)),
                                "items[1].document", "document is required when transferMode is inline"),
                        invalid("inline item carrying a digest", inlineRequest(inlineItem("first"), inlineWithDigest),
                                "items[1].documentDigest", "documentDigest must be absent when transferMode is inline"),
                        invalid("digest request naming no algorithm", digestWithoutAlgorithm, "digestAlgorithm",
                                "digestAlgorithm is required when transferMode is digestOnly"),
                        invalid("digest item without its digest",
                                digestRequest(digestItem("first"), new SignDocumentItemDto("second", null, null)),
                                "items[1].documentDigest",
                                "documentDigest is required when transferMode is digestOnly"),
                        invalid("digest item carrying the document",
                                digestRequest(digestItem("first"), digestWithDocument), "items[1].document",
                                "document must be absent when transferMode is digestOnly"),
                        invalid("digest shorter than its algorithm", digestRequest(digestItem("first"), shortDigest),
                                "items[1].documentDigest",
                                "documentDigest length must match the digest size of digestAlgorithm"),
                        invalid("digest longer than its algorithm", oversizedDigest, "items[0].documentDigest",
                                "documentDigest length must match the digest size of digestAlgorithm"),
                        invalid("null request attribute", nullRequestAttribute, "requestAttributes[0].<list element>",
                                "requestAttributes must not contain null items"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private record Invalid(SignDocumentRequestDto request, String path, String message) {
    }

    private static Named<Invalid> invalid(String name, SignDocumentRequestDto request, String path, String message) {
        return named(name, new Invalid(request, path, message));
    }

    private static SignDocumentRequestDto inlineRequest(SignDocumentItemDto... items) {
        return inlineRequest(List.of(items));
    }

    private static SignDocumentRequestDto inlineRequest(List<SignDocumentItemDto> items) {
        SignDocumentRequestDto request = new SignDocumentRequestDto();
        request.setTransferMode(DocumentTransferMode.INLINE);
        request.setItems(items);
        return request;
    }

    private static SignDocumentRequestDto digestRequest(SignDocumentItemDto... items) {
        return digestRequest(DigestAlgorithm.SHA_256, items);
    }

    private static SignDocumentRequestDto digestRequest(DigestAlgorithm algorithm, SignDocumentItemDto... items) {
        SignDocumentRequestDto request = new SignDocumentRequestDto();
        request.setTransferMode(DocumentTransferMode.DIGEST_ONLY);
        request.setDigestAlgorithm(algorithm);
        request.setItems(List.of(items));
        return request;
    }

    private static SignDocumentItemDto inlineItem(String identifier) {
        return new SignDocumentItemDto(identifier, DOCUMENT, null);
    }

    private static SignDocumentItemDto digestItem(String identifier) {
        return digestItem(identifier, SHA_256_DIGEST);
    }

    private static SignDocumentItemDto digestItem(String identifier, byte[] digest) {
        return new SignDocumentItemDto(identifier, null, digest);
    }
}

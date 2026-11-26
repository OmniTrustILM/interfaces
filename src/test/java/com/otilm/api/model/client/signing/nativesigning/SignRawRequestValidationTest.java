package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ItemIdentifierFormat;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureInputMode;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class SignRawRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final byte[] SHA_256_DIGEST = new byte[DigestAlgorithm.SHA_256.getDigestSizeBytes()];
    private static final byte[] SHA_384_DIGEST = new byte[DigestAlgorithm.SHA_384.getDigestSizeBytes()];
    private static final String FINGERPRINT = "3f9a1c0e5b7d2e4f6a8c0b1d3e5f7a9c1b3d5e7f9a0c2e4b6d8f0a1c3e5b7d9f";
    private static final String FINGERPRINT_MESSAGE = "certificateFingerprint must be 64 hexadecimal characters";
    private static final String LENGTH_MISMATCH = "data length must match the digest size of digestAlgorithm";

    @Test
    void accepts_whenEveryItemCarriesADigestOfTheNamedAlgorithm() {
        // given
        SignRawRequestDto request = request(item("first"), item("second"));

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @Test
    void accepts_whenTheRequestNamesALongerDigestAlgorithm() {
        // given
        SignRawRequestDto request = request(DigestAlgorithm.SHA_384, new SignRawItemDto("first", SHA_384_DIGEST));

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {FINGERPRINT, "3F9A1C0E5B7D2E4F6A8C0B1D3E5F7A9C1B3D5E7F9A0C2E4B6D8F0A1C3E5B7D9F"})
    void accepts_aCertificateFingerprintInEitherCase(String fingerprint) {
        // given
        SignRawRequestDto request = request(item("first"));
        request.setCertificateFingerprint(fingerprint);

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("boundaryIdentifiers")
    void accepts_anIdentifierWithinTheBound(String identifier) {
        // given
        SignRawRequestDto request = request(item(identifier));

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    static Stream<Named<String>> boundaryIdentifiers() {
        return Stream
                .of(named("the longest allowed", "x".repeat(ItemIdentifierFormat.MAX_LENGTH)),
                        named("printable characters beyond ASCII", "smlouva-č.17 – příloha"),
                        named("inner spaces", "release 2026 installer"));
    }

    /** Core refuses data mode with its own message, so the contract lets it through without the digest-length rule. */
    @Test
    void accepts_dataModeWithoutCheckingItemLengths() {
        // given
        SignRawRequestDto request = request(new SignRawItemDto("first", "not a digest".getBytes()));
        request.setSignatureInputMode(SignatureInputMode.DATA);

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void refusesNamingTheField_whenTheRequestViolatesARule(Invalid invalid) {
        // given
        SignRawRequestDto request = invalid.request();

        // when
        Set<ConstraintViolation<SignRawRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(
                violations
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals(invalid.path())
                                && v.getMessage().equals(invalid.message())),
                () -> "expected " + invalid.path() + " '" + invalid.message() + "' in " + violations);
    }

    static Stream<Named<Invalid>> invalidRequests() {
        SignRawRequestDto noMode = request(item("first"));
        noMode.setSignatureInputMode(null);
        SignRawRequestDto noAlgorithm = request(item("first"));
        noAlgorithm.setDigestAlgorithm(null);
        SignRawRequestDto noItems = request();
        noItems.setItems(null);
        SignRawRequestDto oversizedDigest = request(DigestAlgorithm.SHA_384,
                new SignRawItemDto("first", new byte[DigestAlgorithm.SHA_512.getDigestSizeBytes()]));

        return Stream
                .of(invalid("no input mode", noMode, "signatureInputMode", "signatureInputMode is required"),
                        invalid("no digest algorithm", noAlgorithm, "digestAlgorithm", "digestAlgorithm is required"),
                        invalid("empty items", request(), "items", "items must contain at least one item"),
                        invalid("unset items", noItems, "items", "items must contain at least one item"),
                        invalid("null item", request(Collections.singletonList(null)), "items[0].<list element>",
                                "items must not contain null items"),
                        invalid("blank identifier", request(item(" ")), "items[0].identifier",
                                "identifier is required"),
                        invalid("repeated identifier", request(item("same"), item("same")), "items[1].identifier",
                                "identifiers must be unique within the request; duplicates item at index 0"),
                        invalid("identifier over the bound",
                                request(item("x".repeat(ItemIdentifierFormat.MAX_LENGTH + 1))), "items[0].identifier",
                                ItemIdentifierFormat.LENGTH_MESSAGE),
                        invalid("identifier with a line feed", request(item("first\nforged log line")),
                                "items[0].identifier", ItemIdentifierFormat.MESSAGE),
                        invalid("identifier with a NUL", request(item("first\u0000")), "items[0].identifier",
                                ItemIdentifierFormat.MESSAGE),
                        invalid("identifier with DEL", request(item("first\u007F")), "items[0].identifier",
                                ItemIdentifierFormat.MESSAGE),
                        invalid("identifier with a C1 next line", request(item("first\u0085second")),
                                "items[0].identifier", ItemIdentifierFormat.MESSAGE),
                        invalid("item without data", request(item("first"), new SignRawItemDto("second", null)),
                                "items[1].data", "data is required and must not be empty"),
                        invalid("item with empty data", request(new SignRawItemDto("first", new byte[0])),
                                "items[0].data", "data is required and must not be empty"),
                        invalid("digest shorter than its algorithm",
                                request(item("first"), new SignRawItemDto("second", new byte[20])), "items[1].data",
                                LENGTH_MISMATCH),
                        invalid("digest longer than its algorithm", oversizedDigest, "items[0].data", LENGTH_MISMATCH),
                        invalid("fingerprint one character short", pinned(FINGERPRINT.substring(1)),
                                "certificateFingerprint", FINGERPRINT_MESSAGE),
                        invalid("fingerprint one character long", pinned(FINGERPRINT + "0"), "certificateFingerprint",
                                FINGERPRINT_MESSAGE),
                        invalid("fingerprint with colons as openssl prints it", pinned(colonSeparated(FINGERPRINT)),
                                "certificateFingerprint", FINGERPRINT_MESSAGE),
                        invalid("fingerprint with a non-hexadecimal character", pinned("g" + FINGERPRINT.substring(1)),
                                "certificateFingerprint", FINGERPRINT_MESSAGE));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private record Invalid(SignRawRequestDto request, String path, String message) {
    }

    private static Named<Invalid> invalid(String name, SignRawRequestDto request, String path, String message) {
        return named(name, new Invalid(request, path, message));
    }

    private static SignRawRequestDto request(SignRawItemDto... items) {
        return request(List.of(items));
    }

    private static SignRawRequestDto request(List<SignRawItemDto> items) {
        SignRawRequestDto request = new SignRawRequestDto();
        request.setSignatureInputMode(SignatureInputMode.DATA_DIGEST);
        request.setDigestAlgorithm(DigestAlgorithm.SHA_256);
        request.setItems(items);
        return request;
    }

    private static SignRawRequestDto request(DigestAlgorithm algorithm, SignRawItemDto... items) {
        SignRawRequestDto request = request(items);
        request.setDigestAlgorithm(algorithm);
        return request;
    }

    private static SignRawRequestDto pinned(String fingerprint) {
        SignRawRequestDto request = request(item("first"));
        request.setCertificateFingerprint(fingerprint);
        return request;
    }

    private static SignRawItemDto item(String identifier) {
        return new SignRawItemDto(identifier, SHA_256_DIGEST);
    }

    private static String colonSeparated(String hex) {
        StringBuilder separated = new StringBuilder();
        for (int index = 0; index < hex.length(); index += 2) {
            if (index > 0) {
                separated.append(':');
            }
            separated.append(hex, index, index + 2);
        }
        return separated.toString().toUpperCase(Locale.ROOT);
    }
}

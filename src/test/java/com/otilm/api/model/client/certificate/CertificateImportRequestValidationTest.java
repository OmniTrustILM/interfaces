package com.otilm.api.model.client.certificate;

import com.otilm.api.model.core.secret.UploadedFile;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;

class CertificateImportRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final String TOKEN_PROFILE = "8f1c2d3e-4a5b-46c7-88d9-0e1f2a3b4c5d";

    @Test
    void hasNoViolations_whenEachEntryIsNamedOnce() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a"), entry("fingerprint-b"));

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertNoViolations(violations);
    }

    @Test
    void requiresTheFile() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a"));
        request.setFile(null);

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "file", "file is required");
    }

    @Test
    void requiresAtLeastOneEntry() {
        // given
        CertificateImportRequestDto request = request();

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "entries", "entries must contain at least one entry");
    }

    @Test
    void requiresAReferenceOnEveryEntry() {
        // given
        CertificateImportRequestDto request = request(new CertificateImportEntryDto());

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "entries[0].entryReference", "entryReference is required");
    }

    /** Retry safety comes from the entry's own content, so nothing else needs a caller-minted identifier. */
    @Test
    void validatesWithoutAnImportId() {
        // given
        CertificateImportEntryDto entry = new CertificateImportEntryDto();
        entry.setEntryReference("fingerprint-a");

        // when
        Set<ConstraintViolation<CertificateImportEntryDto>> violations = VALIDATOR.validate(entry);

        // then
        assertNoViolations(violations);
    }

    /** Naming one entry twice would import it twice, or leave which of the two applies undefined. */
    @Test
    void refusesTheSameEntryNamedTwice() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a"), entry("fingerprint-a"));

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "eachEntryNamedOnce", "entries must not name the same entryReference twice");
    }

    @Test
    void requiresATokenProfileOnADestinationThatIsGiven() {
        // given
        CertificateImportEntryDto entry = entry("fingerprint-a");
        entry.setKeyDestination(new CertificateEntryKeyDestinationDto());
        CertificateImportRequestDto request = request(entry);

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "entries[0].keyDestination.tokenProfileUuid", "tokenProfileUuid is required");
    }

    private static CertificateImportRequestDto request(CertificateImportEntryDto... entries) {
        CertificateImportRequestDto request = new CertificateImportRequestDto();
        request.setFile(new UploadedFile(new byte[64]));
        request.setEntries(List.of(entries));
        return request;
    }

    private static CertificateImportEntryDto entry(String entryReference) {
        CertificateImportEntryDto entry = new CertificateImportEntryDto();
        entry.setEntryReference(entryReference);
        CertificateEntryKeyDestinationDto destination = new CertificateEntryKeyDestinationDto();
        destination.setTokenProfileUuid(TOKEN_PROFILE);
        entry.setKeyDestination(destination);
        return entry;
    }
}

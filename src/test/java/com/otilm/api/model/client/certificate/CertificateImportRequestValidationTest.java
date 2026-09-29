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
    void hasNoViolations_whenEachEntryIsNamedOnceAndIdentifiedOnce() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a", "import-a"),
                entry("fingerprint-b", "import-b"));

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertNoViolations(violations);
    }

    @Test
    void requiresTheFile() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a", "import-a"));
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
    void requiresAnIdentifierAndAReferenceOnEveryEntry() {
        // given
        CertificateImportRequestDto request = request(new CertificateImportEntryDto());

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "entries[0].entryReference", "entryReference is required");
        assertHasViolation(violations, "entries[0].importId", "importId is required");
    }

    /** Naming one entry twice would import it twice, or leave which of the two applies undefined. */
    @Test
    void refusesTheSameEntryNamedTwice() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a", "import-a"),
                entry("fingerprint-a", "import-b"));

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "eachEntryNamedOnce", "entries must not name the same entryReference twice");
    }

    /** Two entries sharing an identifier would make one of them look like a replay of the other. */
    @Test
    void refusesTwoEntriesSharingAnIdentifier() {
        // given
        CertificateImportRequestDto request = request(entry("fingerprint-a", "import-a"),
                entry("fingerprint-b", "import-a"));

        // when
        Set<ConstraintViolation<CertificateImportRequestDto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "eachImportIdentifiedOnce", "entries must not share an importId");
    }

    @Test
    void requiresATokenProfileOnADestinationThatIsGiven() {
        // given
        CertificateImportEntryDto entry = entry("fingerprint-a", "import-a");
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

    private static CertificateImportEntryDto entry(String entryReference, String importId) {
        CertificateImportEntryDto entry = new CertificateImportEntryDto();
        entry.setEntryReference(entryReference);
        entry.setImportId(importId);
        CertificateEntryKeyDestinationDto destination = new CertificateEntryKeyDestinationDto();
        destination.setTokenProfileUuid(TOKEN_PROFILE);
        entry.setKeyDestination(destination);
        return entry;
    }
}

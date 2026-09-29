package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * An entry's identity comes from {@code entryReference}, its own content, so nothing else may bind a caller-minted
 * identifier onto it.
 */
class CertificateImportEntryDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void refusesAnImportIdAsAnUnknownProperty() {
        String json = "{\"entryReference\":\"fingerprint-a\",\"importId\":\"caller-minted-id\"}";

        assertThrows(UnrecognizedPropertyException.class, () -> mapper.readValue(json, CertificateImportEntryDto.class),
                "importId no longer names a property of the entry");
    }
}

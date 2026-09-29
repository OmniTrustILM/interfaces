package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.inspection.InspectedEntryKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An entry reports what became of each object it produced, so a caller reads {@code certificateOutcome} and
 * {@code keyOutcome} rather than inferring one from {@code imported} alone.
 */
class CertificateImportResultDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesBothOutcomesWithTheirCodes() throws Exception {
        // given
        CertificateImportResultDto result = new CertificateImportResultDto();
        result.setEntryReference("fingerprint-a");
        result.setKind(InspectedEntryKind.KEY_PAIR_WITH_CHAIN);
        result.setImported(true);
        result.setCertificateOutcome(ImportOutcome.CREATED);
        result.setKeyOutcome(ImportOutcome.ADOPTED);

        // when
        String json = mapper.writeValueAsString(result);

        // then
        assertTrue(json.contains("\"certificateOutcome\":\"created\""), json);
        assertTrue(json.contains("\"keyOutcome\":\"adopted\""), json);
    }

    @Test
    void anEntryWithNeitherObjectCarriesNoOutcome() throws Exception {
        // given
        CertificateImportResultDto result = new CertificateImportResultDto();
        result.setEntryReference("fingerprint-a");
        result.setKind(InspectedEntryKind.SIGNING_REQUEST);
        result.setImported(false);

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(result));

        // then
        assertFalse(json.has("certificateOutcome"), json.toString());
        assertFalse(json.has("keyOutcome"), json.toString());
    }
}

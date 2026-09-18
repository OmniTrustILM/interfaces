package com.otilm.api.model.connector.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The v1 certificate payload, which a v2 run reuses. Core reads {@code sequence} and {@code discoveredAt} on the v2
 * path only, so both the names they travel under and their absence on a v1 payload are contract.
 */
class DiscoveryProviderCertificateDataDtoTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void aV2PayloadRoundTripsTheSequenceAndTheObservationTime() throws Exception {
        OffsetDateTime observed = OffsetDateTime.of(2026, 3, 4, 5, 6, 7, 0, ZoneOffset.UTC);
        DiscoveryProviderCertificateDataDto dto = new DiscoveryProviderCertificateDataDto();
        dto.setUuid("e4d1a6d2-0000-4000-8000-000000000001");
        dto.setBase64Content("Zm9v");
        dto.setSequence(17L);
        dto.setDiscoveredAt(observed);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"sequence\":17"), json);
        assertTrue(json.contains("\"discoveredAt\""), json);

        DiscoveryProviderCertificateDataDto back = mapper.readValue(json, DiscoveryProviderCertificateDataDto.class);
        assertEquals(17L, back.getSequence());
        assertEquals(observed.toInstant(), back.getDiscoveredAt().toInstant(),
                "the observation time must survive the trip; a v2 run orders staged items by it");
    }

    @Test
    void aV1PayloadOmitsBothKeysRatherThanSendingThemNull() throws Exception {
        DiscoveryProviderCertificateDataDto dto = new DiscoveryProviderCertificateDataDto();
        dto.setUuid("e4d1a6d2-0000-4000-8000-000000000002");
        dto.setBase64Content("Zm9v");

        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("sequence"), "a v1 provider numbers nothing, so the key must be absent: " + json);
        assertFalse(json.contains("discoveredAt"), "a v1 provider reports no observation time: " + json);

        DiscoveryProviderCertificateDataDto back = mapper.readValue(json, DiscoveryProviderCertificateDataDto.class);
        assertNull(back.getSequence());
        assertNull(back.getDiscoveredAt());
    }

    private static final Validator VALIDATOR = new ValidatorFixture().validator();

    /**
     * The same provider number {@code DiscoveredItemDto} publishes with a minimum of 1: sequence 0 is the cursor's "no
     * items yet" and cannot identify an item. Absent stays legal, since a v1 provider numbers nothing.
     */
    @Test
    void aSequenceBelowOneIsRejectedWhileAbsentStaysLegal() {
        for (long bad : new long[]{0L, -1L}) {
            DiscoveryProviderCertificateDataDto dto = new DiscoveryProviderCertificateDataDto();
            dto.setSequence(bad);
            assertTrue(
                    VALIDATOR.validate(dto).stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                    "sequence " + bad + " must be rejected: the published minimum is 1");
        }
        DiscoveryProviderCertificateDataDto v1 = new DiscoveryProviderCertificateDataDto();
        assertFalse(VALIDATOR.validate(v1).stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                "a v1 payload carries no sequence and must not be refused for it");
        DiscoveryProviderCertificateDataDto first = new DiscoveryProviderCertificateDataDto();
        first.setSequence(1L);
        assertFalse(VALIDATOR.validate(first).stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                "1 is the first legal sequence");
    }

    @Test
    void toStringCarriesTheFieldsThatMakeAStagedItemTraceable() {
        DiscoveryProviderCertificateDataDto dto = new DiscoveryProviderCertificateDataDto();
        dto.setUuid("e4d1a6d2-0000-4000-8000-000000000003");
        dto.setSequence(42L);
        dto.setDiscoveredAt(OffsetDateTime.of(2026, 3, 4, 5, 6, 7, 0, ZoneOffset.UTC));

        String text = dto.toString();

        // Hand-written rather than generated, so a field added to the class is invisible in logs until someone
        // appends it here.
        assertTrue(text.contains("42"),
                "sequence is what ties a staged row back to the connector's numbering: " + text);
        assertTrue(text.contains("2026"),
                "the observation time belongs in a log line about a discovered item: " + text);
    }
}

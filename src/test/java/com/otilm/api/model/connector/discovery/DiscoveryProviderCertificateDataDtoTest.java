package com.otilm.api.model.connector.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    @Test
    void toStringCarriesTheFieldsThatMakeAStagedItemTraceable() {
        DiscoveryProviderCertificateDataDto dto = new DiscoveryProviderCertificateDataDto();
        dto.setUuid("e4d1a6d2-0000-4000-8000-000000000003");
        dto.setSequence(42L);
        dto.setDiscoveredAt(OffsetDateTime.of(2026, 3, 4, 5, 6, 7, 0, ZoneOffset.UTC));

        String text = dto.toString();

        // Hand-written rather than generated, so a field added to the class is invisible in logs until
        // someone remembers to append it -- which is what happened to both of these.
        assertTrue(text.contains("42"),
                "sequence is what ties a staged row back to the connector's numbering: " + text);
        assertTrue(text.contains("2026"),
                "the observation time belongs in a log line about a discovered item: " + text);
    }
}

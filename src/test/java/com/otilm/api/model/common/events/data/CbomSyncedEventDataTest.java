package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CbomSyncedEventDataTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void roundTripsThePublishedEventPayload() throws Exception {
        CbomSyncedEventData event = new CbomSyncedEventData();
        event.setCbomUuid(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
        event.setSerialNumber("urn:uuid:01234567-89ab-cdef-0123-456789abcdef");
        event.setVersion(2);
        event.setSource("scanner");
        event.setTotalAssets(12);
        event.setAssetsSyncedAt(OffsetDateTime.parse("2026-10-13T10:20:30.123456Z"));

        String payload = mapper.writeValueAsString(event);
        var json = mapper.readTree(payload);

        assertEquals("01234567-89ab-cdef-0123-456789abcdef", json.path("cbomUuid").asText());
        assertEquals("urn:uuid:01234567-89ab-cdef-0123-456789abcdef", json.path("serialNumber").asText());
        assertEquals(2, json.path("version").asInt());
        assertEquals("scanner", json.path("source").asText());
        assertEquals(12, json.path("totalAssets").asInt());
        assertEquals("2026-10-13T10:20:30.123456Z", json.path("assetsSyncedAt").asText());
        assertEquals(event, mapper.readValue(payload, CbomSyncedEventData.class));
    }

    @Test
    void readsAnEventWithoutTheOptionalSource() throws Exception {
        String payload = """
                {
                  "cbomUuid": "01234567-89ab-cdef-0123-456789abcdef",
                  "serialNumber": "urn:uuid:01234567-89ab-cdef-0123-456789abcdef",
                  "version": 2,
                  "totalAssets": 12,
                  "assetsSyncedAt": "2026-10-13T10:20:30.123456Z"
                }
                """;

        CbomSyncedEventData event = mapper.readValue(payload, CbomSyncedEventData.class);

        assertNull(event.getSource());
        assertEquals(OffsetDateTime.parse("2026-10-13T10:20:30.123456Z"), event.getAssetsSyncedAt());
        assertFalse(mapper.readTree(mapper.writeValueAsString(event)).has("source"));
    }

    @Test
    void thePublishedSchemaRequiresTheEventFieldsAndAllowsAnAbsentSource() {
        var schema = OpenApiSchemaTestSupport.openApi31Schema(CbomSyncedEventData.class);

        assertEquals(Set.of("cbomUuid", "serialNumber", "version", "totalAssets", "assetsSyncedAt"),
                new HashSet<>(schema.getRequired()));
        assertTrue(schema.getProperties().containsKey("source"));
        assertEquals("date-time", schema.getProperties().get("assetsSyncedAt").getFormat());
    }
}

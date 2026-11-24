package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.core.certificate.RenewalWindowSource;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRenewalDueEventDataTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void roundTripsThroughJson() throws Exception {
        CertificateRenewalDueEventData data = new CertificateRenewalDueEventData();
        data.setExpiresAt(ZonedDateTime.parse("2026-12-01T00:00:00Z"));
        data.setWindowStart(ZonedDateTime.parse("2026-11-20T10:00:00Z"));
        data.setWindowEnd(ZonedDateTime.parse("2026-11-22T10:00:00Z"));
        data.setExplanationUrl("https://ca.example.com/explain");
        data.setSource(RenewalWindowSource.AUTHORITY);
        data.setAdvanced(true);

        String json = mapper.writeValueAsString(data);
        assertTrue(json.contains("\"source\":\"AUTHORITY\"") || json.contains("\"source\":\"authority\""), json);

        CertificateRenewalDueEventData back = mapper.readValue(json, CertificateRenewalDueEventData.class);
        assertEquals(data.getWindowStart().toInstant(), back.getWindowStart().toInstant());
        assertEquals(data.getWindowEnd().toInstant(), back.getWindowEnd().toInstant());
        assertEquals(data.getExpiresAt().toInstant(), back.getExpiresAt().toInstant());
        assertEquals("https://ca.example.com/explain", back.getExplanationUrl());
        assertEquals(RenewalWindowSource.AUTHORITY, back.getSource());
        assertTrue(back.isAdvanced());
    }

    @Test
    void advancedDefaultsToFalse() {
        assertFalse(new CertificateRenewalDueEventData().isAdvanced());
    }
}

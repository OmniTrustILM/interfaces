package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRegisteredEventDataTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void toStringExcludesCredentialButKeepsOtherFields() {
        CertificateRegisteredEventData data = new CertificateRegisteredEventData();
        data.setCompletionDeadline(ZonedDateTime.parse("2026-08-01T00:00:00Z"));
        data.setCredential("s3cret-challenge-value");

        String rendered = data.toString();
        assertFalse(rendered.contains("s3cret-challenge-value"),
                "the credential must never appear in toString (both event envelopes carry this object in their toString → logs/tracing spans)");
        assertFalse(rendered.contains("credential"), "the credential field must be excluded from toString");
        assertTrue(rendered.contains("completionDeadline"),
                "a non-excluded field must still render — proves the exclusion is selective, not an empty toString");
    }

    @Test
    void credentialSerializesToJsonForExternalDelivery() throws Exception {
        CertificateRegisteredEventData data = new CertificateRegisteredEventData();
        data.setCredential("s3cret-challenge-value");

        String json = mapper.writeValueAsString(data);
        // The whole design depends on the credential reaching external notification providers via JSON — a stray
        // @JsonIgnore (plausible next to the toString/equals exclusions) would silently break delivery.
        assertTrue(json.contains("s3cret-challenge-value"), "the credential must serialize to JSON for external delivery");

        CertificateRegisteredEventData back = mapper.readValue(json, CertificateRegisteredEventData.class);
        assertEquals("s3cret-challenge-value", back.getCredential(), "the credential must round-trip through JSON");
    }

    @Test
    void credentialExcludedFromEqualsAndHashCode() {
        CertificateRegisteredEventData a = new CertificateRegisteredEventData();
        a.setCredential("secret-a");
        CertificateRegisteredEventData b = new CertificateRegisteredEventData();
        b.setCredential("secret-b");
        assertEquals(a, b, "the credential must not affect equals()");
        assertEquals(a.hashCode(), b.hashCode(), "the credential must not affect hashCode()");
    }
}

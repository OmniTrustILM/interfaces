package com.otilm.api.model.common.events.data;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CertificateRegisteredEventDataTest {

    @Test
    void toStringExcludesCredential() {
        CertificateRegisteredEventData data = new CertificateRegisteredEventData();
        data.setSubjectDn("CN=device-7");
        data.setIssuanceDeadline(ZonedDateTime.parse("2026-08-01T00:00:00Z"));
        data.setCredential("s3cret-challenge-value");

        String rendered = data.toString();
        assertFalse(rendered.contains("s3cret-challenge-value"),
                "the credential must never appear in toString (both event envelopes carry this object in their toString → logs/tracing spans)");
        assertFalse(rendered.contains("credential"), "the credential field must be excluded from toString");
    }

    @Test
    void credentialIsReadableForExternalDelivery() {
        CertificateRegisteredEventData data = new CertificateRegisteredEventData();
        data.setCredential("s3cret");
        // Still accessible (and JSON-serializable) so the external notification provider receives it.
        assertEquals("s3cret", data.getCredential());
    }
}

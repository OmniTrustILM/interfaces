package com.otilm.api.model.common.events.data;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CertificateEventDataTest {

    @Test
    void leavesOutTheFieldsTheCertificateDoesNotHave_whenItsTypeHasNoSerialNumberOrSubjectDn() {
        // given
        UUID uuid = UUID.randomUUID();
        String displayName = "ssh-key-id";
        CertificateEventData eventData = new CertificateEventData();
        eventData.setCertificateUuid(uuid);
        eventData.setDisplayName(displayName);

        // when
        String text = eventData.toString();

        // then
        assertEquals("Certificate: {certificateUuid=%s, displayName='%s'}".formatted(uuid, displayName), text);
        assertFalse(text.contains("null"));
    }

    @Test
    void listsEveryFieldTheCertificateHas_whenItHasThemAll() {
        // given
        UUID uuid = UUID.randomUUID();
        CertificateEventData eventData = new CertificateEventData();
        eventData.setCertificateUuid(uuid);
        eventData.setFingerprint("abc");
        eventData.setDisplayName("holder");
        eventData.setSerialNumber("1a2b");
        eventData.setSubjectDn("CN=holder");
        eventData.setIssuerDn("CN=issuer");

        // when
        String text = eventData.toString();

        // then
        String expected = ("Certificate: {certificateUuid=%s, fingerprint='abc', displayName='holder', "
                + "serialNumber='1a2b', subjectDn='CN=holder', issuerDn='CN=issuer'}").formatted(uuid);
        assertEquals(expected, text);
    }
}

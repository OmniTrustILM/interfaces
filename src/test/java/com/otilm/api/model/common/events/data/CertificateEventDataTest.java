package com.otilm.api.model.common.events.data;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateEventDataTest {

    @Test
    void namesTheCertificateByDisplayName_whenItHasNoSerialNumberOrSubjectDn() {
        // given
        CertificateEventData eventData = new CertificateEventData();
        eventData.setCertificateUuid(UUID.randomUUID());
        eventData.setFingerprint("abc");
        eventData.setDisplayName("ssh-key-id");

        // when
        String text = eventData.toString();

        // then
        assertTrue(text.contains("displayName='ssh-key-id'"));
        assertTrue(text.contains("serialNumber='null'"));
    }
}

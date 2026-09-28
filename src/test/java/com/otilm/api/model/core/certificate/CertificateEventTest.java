package com.otilm.api.model.core.certificate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateEventTest {

    @Test
    void findByCodeFindsTheKeystoreDownload() {
        assertEquals(CertificateEvent.DOWNLOAD_KEYSTORE, CertificateEvent.findByCode("Download Keystore"));
    }

    @Test
    void detailSaysWhetherTheKeystoreIsAvailable() {
        CertificateDetailDto detail = new CertificateDetailDto();

        detail.setKeystoreAvailable(true);

        assertTrue(detail.isKeystoreAvailable());
    }
}

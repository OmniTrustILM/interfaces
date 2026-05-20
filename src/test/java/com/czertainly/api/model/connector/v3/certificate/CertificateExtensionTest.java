package com.czertainly.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateExtensionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripPreservesAllFields() throws Exception {
        CertificateExtension ext = new CertificateExtension();
        ext.setOid("2.5.29.37");
        ext.setCritical(true);
        ext.setValueBase64("MA0GCysGAQQBgjcVAQUDAg==");

        String json = mapper.writeValueAsString(ext);
        CertificateExtension back = mapper.readValue(json, CertificateExtension.class);

        assertEquals("2.5.29.37", back.getOid());
        assertTrue(back.isCritical());
        assertEquals("MA0GCysGAQQBgjcVAQUDAg==", back.getValueBase64());
    }
}

package com.czertainly.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CertificateRegistrationRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsAllFields() throws Exception {
        CertificateExtension ext = new CertificateExtension();
        ext.setOid("2.5.29.37");
        ext.setCritical(false);
        ext.setValueBase64("MA0GCysGAQQBgjcVAQUDAg==");

        CertificateRegistrationRequestDtoV3 dto = new CertificateRegistrationRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setSubjectDn("CN=device-7,O=Acme");
        dto.setSubjectAltName("DNS:device-7.acme.local");
        dto.setExtensions(List.of(ext));
        dto.setAttributes(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateRegistrationRequestDtoV3 back =
                mapper.readValue(json, CertificateRegistrationRequestDtoV3.class);
        assertEquals("CN=device-7,O=Acme", back.getSubjectDn());
        assertEquals("DNS:device-7.acme.local", back.getSubjectAltName());
        assertEquals(1, back.getExtensions().size());
        assertEquals("2.5.29.37", back.getExtensions().get(0).getOid());
    }
}

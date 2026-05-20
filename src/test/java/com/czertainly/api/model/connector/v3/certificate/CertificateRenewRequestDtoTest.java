package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.core.enums.CertificateRequestFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRenewRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsAllFields() throws Exception {
        CertificateRenewRequestDto dto = new CertificateRenewRequestDto();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setRequest("MIICij...");
        dto.setFormat(CertificateRequestFormat.PKCS10);
        dto.setExistingCertificate("MIIBkjCCATs...");
        dto.setReuseKey(false);
        dto.setAttributes(List.of());
        dto.setMeta(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateRenewRequestDto back = mapper.readValue(json, CertificateRenewRequestDto.class);
        assertEquals("MIICij...", back.getRequest());
        assertEquals("MIIBkjCCATs...", back.getExistingCertificate());
        assertEquals(false, back.isReuseKey());
    }

    @Test
    void reuseKeyDefaultsFalse() throws Exception {
        String json = "{\"authorityAttributes\":[],\"raProfileAttributes\":[],\"existingCertificate\":\"X\"}";
        CertificateRenewRequestDto back = mapper.readValue(json, CertificateRenewRequestDto.class);
        assertTrue(!back.isReuseKey());
    }
}

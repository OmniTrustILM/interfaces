package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.core.enums.CertificateRequestFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CertificateSignRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsAllFields() throws Exception {
        CertificateSignRequestDto dto = new CertificateSignRequestDto();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setRequest("MIICij...");
        dto.setFormat(CertificateRequestFormat.PKCS10);
        dto.setAttributes(List.of());
        dto.setRegistrationMeta(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateSignRequestDto back = mapper.readValue(json, CertificateSignRequestDto.class);
        assertEquals("MIICij...", back.getRequest());
        assertEquals(CertificateRequestFormat.PKCS10, back.getFormat());
    }

    @Test
    void registrationMetaOptional() throws Exception {
        String json = "{\"authorityAttributes\":[],\"raProfileAttributes\":[],\"request\":\"X\"}";
        CertificateSignRequestDto back = mapper.readValue(json, CertificateSignRequestDto.class);
        assertNull(back.getRegistrationMeta());
    }
}

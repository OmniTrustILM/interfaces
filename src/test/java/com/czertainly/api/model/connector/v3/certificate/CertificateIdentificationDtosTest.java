package com.czertainly.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CertificateIdentificationDtosTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestRoundTrips() throws Exception {
        CertificateIdentificationRequestDto dto = new CertificateIdentificationRequestDto();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setCertificate("MIIBkjCCATs...");
        String json = mapper.writeValueAsString(dto);
        CertificateIdentificationRequestDto back =
                mapper.readValue(json, CertificateIdentificationRequestDto.class);
        assertEquals("MIIBkjCCATs...", back.getCertificate());
    }

    @Test
    void responseRoundTrips() throws Exception {
        CertificateIdentificationResponseDto dto = new CertificateIdentificationResponseDto();
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateIdentificationResponseDto back =
                mapper.readValue(json, CertificateIdentificationResponseDto.class);
        assertEquals(0, back.getMeta().size());
    }
}

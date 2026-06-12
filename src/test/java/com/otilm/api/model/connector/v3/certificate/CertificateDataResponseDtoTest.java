package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.certificate.CertificateType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CertificateDataResponseDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsWithCertAndMeta() throws Exception {
        CertificateDataResponseDto dto = new CertificateDataResponseDto();
        dto.setCertificateData("MIIBkjCCATs...");
        dto.setMeta(List.of());
        dto.setCertificateType(CertificateType.X509);

        String json = mapper.writeValueAsString(dto);
        CertificateDataResponseDto back = mapper.readValue(json, CertificateDataResponseDto.class);

        assertEquals("MIIBkjCCATs...", back.getCertificateData());
        assertEquals(CertificateType.X509, back.getCertificateType());
        assertEquals(0, back.getMeta().size());
    }

    @Test
    void async202ShapeNullCertWithMeta() throws Exception {
        CertificateDataResponseDto dto = new CertificateDataResponseDto();
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateDataResponseDto back = mapper.readValue(json, CertificateDataResponseDto.class);
        assertNull(back.getCertificateData());
    }
}

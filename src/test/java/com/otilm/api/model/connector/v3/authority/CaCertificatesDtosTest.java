package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.otilm.api.model.core.certificate.CertificateType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaCertificatesDtosTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestRoundTrips() throws Exception {
        CaCertificatesRequestDtoV3 dto = new CaCertificatesRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        String json = mapper.writeValueAsString(dto);
        CaCertificatesRequestDtoV3 back = mapper.readValue(json, CaCertificatesRequestDtoV3.class);
        assertEquals(0, back.getAuthorityAttributes().size());
    }

    @Test
    void responseRoundTripsChain() throws Exception {
        CertificateDataResponseDto issuing = new CertificateDataResponseDto();
        issuing.setCertificateData("issuing-base64");
        issuing.setCertificateType(CertificateType.X509);

        CertificateDataResponseDto root = new CertificateDataResponseDto();
        root.setCertificateData("root-base64");
        root.setCertificateType(CertificateType.X509);

        CaCertificatesResponseDto dto = new CaCertificatesResponseDto();
        dto.setCertificates(List.of(issuing, root));

        String json = mapper.writeValueAsString(dto);
        CaCertificatesResponseDto back = mapper.readValue(json, CaCertificatesResponseDto.class);
        assertEquals(2, back.getCertificates().size());
        assertEquals("issuing-base64", back.getCertificates().get(0).getCertificateData());
        assertEquals("root-base64", back.getCertificates().get(1).getCertificateData());
    }
}

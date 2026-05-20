package com.czertainly.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusDtosTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void statusResponseRoundTrips() throws Exception {
        CertificateOperationStatusResponseDto dto = new CertificateOperationStatusResponseDto();
        dto.setStatus(CertificateOperationStatus.COMPLETED);
        dto.setCertificateData("MIIBkjCCATs...");
        dto.setMeta(List.of());
        dto.setReason(null);

        String json = mapper.writeValueAsString(dto);
        CertificateOperationStatusResponseDto back =
                mapper.readValue(json, CertificateOperationStatusResponseDto.class);
        assertEquals(CertificateOperationStatus.COMPLETED, back.getStatus());
        assertEquals("MIIBkjCCATs...", back.getCertificateData());
    }

    @Test
    void statusRequestRoundTrips() throws Exception {
        CertificateOperationStatusRequestDto dto = new CertificateOperationStatusRequestDto();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateOperationStatusRequestDto back =
                mapper.readValue(json, CertificateOperationStatusRequestDto.class);
        assertEquals(0, back.getMeta().size());
    }

    @Test
    void cancelRequestRoundTrips() throws Exception {
        CertificateOperationCancelRequestDto dto = new CertificateOperationCancelRequestDto();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateOperationCancelRequestDto back =
                mapper.readValue(json, CertificateOperationCancelRequestDto.class);
        assertEquals(0, back.getMeta().size());
    }
}

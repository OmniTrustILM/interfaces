package com.otilm.api.model.connector.v3.certificate;

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
        CertificateOperationStatusRequestDtoV3 dto = new CertificateOperationStatusRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateOperationStatusRequestDtoV3 back =
                mapper.readValue(json, CertificateOperationStatusRequestDtoV3.class);
        assertEquals(0, back.getMeta().size());
    }

    @Test
    void cancelRequestRoundTrips() throws Exception {
        CertificateOperationCancelRequestDtoV3 dto = new CertificateOperationCancelRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());
        String json = mapper.writeValueAsString(dto);
        CertificateOperationCancelRequestDtoV3 back =
                mapper.readValue(json, CertificateOperationCancelRequestDtoV3.class);
        assertEquals(0, back.getMeta().size());
    }
}

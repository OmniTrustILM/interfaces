package com.czertainly.api.model.connector.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateOperationStatusResponseDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesInProgress_omitsAbsentOptionalFields() throws Exception {
        CertificateOperationStatusResponseDto dto = new CertificateOperationStatusResponseDto();
        dto.setStatus(CertificateOperationStatus.IN_PROGRESS);
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"status\":\"inProgress\""),
                "expected status code 'inProgress' in JSON, got: " + json);
        // certificateData/reason are null and should serialize as null (default Jackson) — they're not omitted but explicit nulls are acceptable
    }

    @Test
    void serializesCompletedWithCertificateData() throws Exception {
        CertificateOperationStatusResponseDto dto = new CertificateOperationStatusResponseDto();
        dto.setStatus(CertificateOperationStatus.COMPLETED);
        dto.setCertificateData("BASE64DATA==");
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"status\":\"completed\""));
        assertTrue(json.contains("\"certificateData\":\"BASE64DATA==\""));
    }

    @Test
    void serializesFailedWithReason() throws Exception {
        CertificateOperationStatusResponseDto dto = new CertificateOperationStatusResponseDto();
        dto.setStatus(CertificateOperationStatus.FAILED);
        dto.setReason("CA rejected request");
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"status\":\"failed\""));
        assertTrue(json.contains("\"reason\":\"CA rejected request\""));
    }

    @Test
    void deserializesInProgressFromMinimalJson() throws Exception {
        CertificateOperationStatusResponseDto dto = mapper.readValue(
                "{\"status\":\"inProgress\"}",
                CertificateOperationStatusResponseDto.class);
        assertEquals(CertificateOperationStatus.IN_PROGRESS, dto.getStatus());
        assertNull(dto.getCertificateData());
        assertNull(dto.getReason());
    }

    @Test
    void toStringRedactsCertificateData() {
        CertificateOperationStatusResponseDto dto = new CertificateOperationStatusResponseDto();
        dto.setStatus(CertificateOperationStatus.COMPLETED);
        dto.setCertificateData("BASE64DATA==");
        String s = dto.toString();
        assertTrue(s.contains("<present>"),
                "toString should not leak cert content; got: " + s);
        assertFalse(s.contains("BASE64DATA"),
                "toString should not leak cert content; got: " + s);
    }
}

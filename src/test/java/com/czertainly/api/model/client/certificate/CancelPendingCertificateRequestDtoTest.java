package com.czertainly.api.model.client.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CancelPendingCertificateRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesEmptyBody() throws Exception {
        CancelPendingCertificateRequestDto dto =
                mapper.readValue("{}", CancelPendingCertificateRequestDto.class);
        assertNull(dto.getReason());
    }

    @Test
    void deserializesWithReason() throws Exception {
        CancelPendingCertificateRequestDto dto = mapper.readValue(
                "{\"reason\":\"requirement changed\"}",
                CancelPendingCertificateRequestDto.class);
        assertEquals("requirement changed", dto.getReason());
    }

    @Test
    void roundTripsReason() throws Exception {
        CancelPendingCertificateRequestDto dto = new CancelPendingCertificateRequestDto();
        dto.setReason("ops decision reversed");

        String json = mapper.writeValueAsString(dto);
        CancelPendingCertificateRequestDto back =
                mapper.readValue(json, CancelPendingCertificateRequestDto.class);

        assertEquals("ops decision reversed", back.getReason());
    }
}

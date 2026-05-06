package com.czertainly.api.model.connector.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateOperationCancelRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsEmptyLists() throws Exception {
        CertificateOperationCancelRequestDto dto = new CertificateOperationCancelRequestDto();
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateOperationCancelRequestDto back =
                mapper.readValue(json, CertificateOperationCancelRequestDto.class);

        assertNotNull(back);
        assertNotNull(back.getRaProfileAttributes());
        assertNotNull(back.getMeta());
        assertTrue(back.getRaProfileAttributes().isEmpty());
        assertTrue(back.getMeta().isEmpty());
    }

    @Test
    void deserializesWithoutMeta() throws Exception {
        // meta is optional; an Authority Provider that omits it must still parse cleanly
        String json = "{\"raProfileAttributes\":[]}";
        CertificateOperationCancelRequestDto dto =
                mapper.readValue(json, CertificateOperationCancelRequestDto.class);
        assertNotNull(dto.getRaProfileAttributes());
    }

    @Test
    void toStringIncludesBothFields() {
        CertificateOperationCancelRequestDto dto = new CertificateOperationCancelRequestDto();
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());

        String s = dto.toString();
        assertTrue(s.contains("raProfileAttributes"));
        assertTrue(s.contains("meta"));
    }
}

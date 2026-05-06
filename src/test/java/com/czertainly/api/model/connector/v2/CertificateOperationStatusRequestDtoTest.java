package com.czertainly.api.model.connector.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateOperationStatusRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsEmptyLists() throws Exception {
        CertificateOperationStatusRequestDto dto = new CertificateOperationStatusRequestDto();
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateOperationStatusRequestDto back =
                mapper.readValue(json, CertificateOperationStatusRequestDto.class);

        assertNotNull(back);
        assertNotNull(back.getRaProfileAttributes());
        assertNotNull(back.getMeta());
        assertTrue(back.getRaProfileAttributes().isEmpty());
        assertTrue(back.getMeta().isEmpty());
    }

    @Test
    void deserializesWithoutMeta() throws Exception {
        String json = "{\"raProfileAttributes\":[]}";
        CertificateOperationStatusRequestDto dto =
                mapper.readValue(json, CertificateOperationStatusRequestDto.class);
        assertNotNull(dto.getRaProfileAttributes());
    }

    @Test
    void toStringIncludesBothFields() {
        CertificateOperationStatusRequestDto dto = new CertificateOperationStatusRequestDto();
        dto.setRaProfileAttributes(List.of());
        dto.setMeta(List.of());

        String s = dto.toString();
        assertTrue(s.contains("raProfileAttributes"));
        assertTrue(s.contains("meta"));
    }
}

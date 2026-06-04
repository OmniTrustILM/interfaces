package com.czertainly.api.model.connector.v3.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrlDtosTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestRoundTripsDeltaFlag() throws Exception {
        CrlRequestDtoV3 dto = new CrlRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setDelta(true);
        String json = mapper.writeValueAsString(dto);
        CrlRequestDtoV3 back = mapper.readValue(json, CrlRequestDtoV3.class);
        assertTrue(back.isDelta());
    }

    @Test
    void responseRoundTripsCrl() throws Exception {
        CrlResponseDto dto = new CrlResponseDto();
        dto.setCrl("MIIBkjCCATs...");
        String json = mapper.writeValueAsString(dto);
        CrlResponseDto back = mapper.readValue(json, CrlResponseDto.class);
        assertEquals("MIIBkjCCATs...", back.getCrl());
    }
}

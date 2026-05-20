package com.czertainly.api.model.connector.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class V3AuthorityScopedRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /** Concrete subclass for testing the abstract base. */
    static class Probe extends V3AuthorityScopedRequestDto {}

    @Test
    void roundTripsEmptyLists() throws Exception {
        Probe dto = new Probe();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        String json = mapper.writeValueAsString(dto);
        Probe back = mapper.readValue(json, Probe.class);
        assertNotNull(back.getAuthorityAttributes());
        assertNotNull(back.getRaProfileAttributes());
        assertTrue(back.getAuthorityAttributes().isEmpty());
        assertTrue(back.getRaProfileAttributes().isEmpty());
    }
}

package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.authority.CertificateRevocationReason;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CertificateRevocationRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsAllFields() throws Exception {
        CertificateRevocationRequestDtoV3 dto = new CertificateRevocationRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setCertificate("MIIBkjCCATs...");
        dto.setReason(CertificateRevocationReason.KEY_COMPROMISE);
        dto.setAttributes(List.of());
        dto.setMeta(List.of());

        String json = mapper.writeValueAsString(dto);
        CertificateRevocationRequestDtoV3 back =
                mapper.readValue(json, CertificateRevocationRequestDtoV3.class);
        assertEquals("MIIBkjCCATs...", back.getCertificate());
        assertEquals(CertificateRevocationReason.KEY_COMPROMISE, back.getReason());
    }
}

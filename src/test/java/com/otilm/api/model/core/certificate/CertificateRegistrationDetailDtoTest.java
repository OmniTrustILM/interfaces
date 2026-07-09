package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CertificateRegistrationDetailDtoTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void registrationBlockRoundTrips() throws Exception {
        CertificateRegistrationDetailDto reg = new CertificateRegistrationDetailDto();
        reg.setState("ACTIVE");
        reg.setExpiresAt(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        reg.setFailedAttempts(2);

        CertificateRegistrationDetailDto back =
                mapper.readValue(mapper.writeValueAsString(reg), CertificateRegistrationDetailDto.class);
        assertEquals("ACTIVE", back.getState());
        assertEquals(reg.getExpiresAt(), back.getExpiresAt());
        assertEquals(2, back.getFailedAttempts());
    }

    @Test
    void certificateDetailExposesRegistrationBlock() {
        CertificateRegistrationDetailDto reg = new CertificateRegistrationDetailDto();
        CertificateDetailDto detail = new CertificateDetailDto();
        detail.setRegistration(reg);
        assertSame(reg, detail.getRegistration());
    }
}

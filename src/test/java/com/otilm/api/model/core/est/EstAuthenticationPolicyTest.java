package com.otilm.api.model.core.est;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.enums.CertificateProtocol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstAuthenticationPolicyTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void codesTravelAsJsonStrings() throws Exception {
        assertEquals("\"certificateRegistration\"",
                mapper.writeValueAsString(EstAuthenticationPolicy.CERTIFICATE_REGISTRATION));
        assertEquals("\"raBasic\"", mapper.writeValueAsString(EstAuthenticationPolicy.RA_BASIC));
        assertEquals("\"vendorMtls\"", mapper.writeValueAsString(EstAuthenticationPolicy.VENDOR_MTLS));
        assertEquals("\"raMtls\"", mapper.writeValueAsString(EstAuthenticationPolicy.RA_MTLS));
        assertEquals(EstAuthenticationPolicy.RA_MTLS, mapper.readValue("\"raMtls\"", EstAuthenticationPolicy.class));
    }

    @Test
    void unknownCodeIsAValidationErrorNamingTheCode() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> EstAuthenticationPolicy.findByCode("RA_BASIC"));
        assertTrue(e.getMessage().contains("RA_BASIC"));
    }

    @Test
    void estIsACertificateProtocolWithItsOwnProfileResource() throws Exception {
        assertEquals("\"est\"", mapper.writeValueAsString(CertificateProtocol.EST));
        assertEquals("estProfiles", Resource.EST_PROFILE.getCode());
    }
}

package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrustedCertificateEnumsTest {

    @Test
    void purposeCodesAreStable() {
        assertEquals("validation", TrustedCertificatePurpose.VALIDATION.getCode());
        assertEquals("platform", TrustedCertificatePurpose.PLATFORM.getCode());
        assertEquals(TrustedCertificatePurpose.PLATFORM, TrustedCertificatePurpose.findByCode("platform"));
    }

    @Test
    void platformStateHasEightValuesAndRejectsUnknownCode() {
        assertEquals(8, TrustedCertificatePlatformState.values().length);
        assertEquals("removalFailed", TrustedCertificatePlatformState.REMOVAL_FAILED.getCode());
        assertThrows(ValidationException.class, () -> TrustedCertificatePlatformState.findByCode("gone"));
    }

    @Test
    void platformSourceRoundTripsThroughJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals("\"deployment\"", mapper.writeValueAsString(TrustedCertificatePlatformSource.DEPLOYMENT));
        assertEquals(TrustedCertificatePlatformSource.NONE,
                mapper.readValue("\"none\"", TrustedCertificatePlatformSource.class));
    }
}

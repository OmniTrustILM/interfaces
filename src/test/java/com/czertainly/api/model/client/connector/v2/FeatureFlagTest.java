package com.czertainly.api.model.client.connector.v2;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagTest {

    @Test
    void certificatePreRegistrationFlagExists() {
        FeatureFlag flag = FeatureFlag.CERTIFICATE_PRE_REGISTRATION;
        assertEquals("certificatePreRegistration", flag.getCode());
        assertEquals(List.of(ConnectorInterface.AUTHORITY), flag.getApplicableInterfaces());
    }

    @Test
    void findByCodeRoundTripsAllEntries() {
        for (FeatureFlag f : FeatureFlag.values()) {
            assertEquals(f, FeatureFlag.findByCode(f.getCode()));
        }
        assertTrue(true);
    }
}

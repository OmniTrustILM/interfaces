package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.client.connector.v2.FeatureFlag;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscoveryV2EnumsTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void runStateSerializesToCode() throws Exception {
        assertEquals("\"running\"", mapper.writeValueAsString(DiscoveryRunState.RUNNING));
        assertEquals(DiscoveryRunState.STOPPED, mapper.readValue("\"stopped\"", DiscoveryRunState.class));
    }

    @Test
    void unknownCodeThrowsValidationException() {
        assertThrows(ValidationException.class, () -> DiscoveryRunState.findByCode("bogus"));
    }

    @Test
    void discoveryStatusGainsStoppedAndCancelled() {
        assertEquals("stopped", DiscoveryStatus.STOPPED.getCode());
        assertEquals("cancelled", DiscoveryStatus.CANCELLED.getCode());
    }

    @Test
    void discoveryFlagsAreEnforcedAndScopedToDiscovery() {
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.DISCOVERY_STOP_RESUME.getBehavior());
        assertEquals(List.of(ConnectorInterface.DISCOVERY),
                FeatureFlag.DISCOVERY_STOP_RESUME.getApplicableInterfaces());
        assertEquals(FeatureFlag.DISCOVERY_STOP_RESUME, FeatureFlag.findByCode("discoveryStopResume"));
    }

}

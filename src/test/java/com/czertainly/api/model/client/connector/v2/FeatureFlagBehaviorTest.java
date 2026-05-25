package com.czertainly.api.model.client.connector.v2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeatureFlagBehaviorTest {

    @Test
    void everyFlagDeclaresBehavior() {
        for (FeatureFlag flag : FeatureFlag.values()) {
            assertNotNull(flag.getBehavior(),
                "FeatureFlag " + flag + " must declare a FeatureFlagBehavior");
        }
    }

    @Test
    void existingFlagsClassifiedCorrectly() {
        assertEquals(FeatureFlag.FeatureFlagBehavior.INFORMATIONAL, FeatureFlag.STATELESS.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.INFORMATIONAL, FeatureFlag.OPEN_METRICS.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.SECRET_VERSIONING.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.SECRET_ROTATION.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.CONTENT_SIGNING.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.TIMESTAMPING.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.CERTIFICATE_REGISTRATION.getBehavior());
    }
}

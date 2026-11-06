package com.otilm.api.model.client.connector.v2;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FeatureFlagBehaviorTest {

    private static final List<FeatureFlag> LEVEL_RUNGS = List
            .of(FeatureFlag.LEVEL_TIMESTAMPED, FeatureFlag.LEVEL_LONG_TERM, FeatureFlag.LEVEL_ARCHIVAL);

    @Test
    void everyFlagDeclaresBehavior() {
        for (FeatureFlag flag : FeatureFlag.values()) {
            assertNotNull(flag.getBehavior(), "FeatureFlag " + flag + " must declare a FeatureFlagBehavior");
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
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.CERTIFICATE_STATUS_POLLING.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.KEY_IMPORT.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.KEY_EXPORT.getBehavior());
        assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, FeatureFlag.PREHASHED_SIGNING.getBehavior());
    }

    /** A rung is an opt-in capability claim, so an absent one must read as "not reached" rather than as unknown. */
    @Test
    void everyLevelRungIsEnforced() {
        for (FeatureFlag rung : LEVEL_RUNGS) {
            assertEquals(FeatureFlag.FeatureFlagBehavior.ENFORCED, rung.getBehavior(), rung.name());
        }
    }

    /** Rungs are declared per implemented family, so each applies to exactly the four family interfaces. */
    @Test
    void everyLevelRungAppliesToTheFourFamilyInterfaces() {
        List<ConnectorInterface> familyInterfaces = List
                .of(ConnectorInterface.PADES_FORMATTING, ConnectorInterface.XADES_FORMATTING,
                        ConnectorInterface.CADES_FORMATTING, ConnectorInterface.JADES_FORMATTING);

        assertEquals(familyInterfaces, FeatureFlag.CONTENT_SIGNING.getApplicableInterfaces());
        for (FeatureFlag rung : LEVEL_RUNGS) {
            assertEquals(familyInterfaces, rung.getApplicableInterfaces(), rung.name());
        }
    }

    /**
     * The ladder is a prefix: reaching a rung means reaching every rung below it, so a connector declaring one without
     * its prerequisite has made a malformed claim rather than a narrower one.
     */
    @Test
    void levelRungsChainToTheirPrerequisite() {
        assertEquals(FeatureFlag.CONTENT_SIGNING, FeatureFlag.LEVEL_TIMESTAMPED.getPrerequisite());
        assertEquals(FeatureFlag.LEVEL_TIMESTAMPED, FeatureFlag.LEVEL_LONG_TERM.getPrerequisite());
        assertEquals(FeatureFlag.LEVEL_LONG_TERM, FeatureFlag.LEVEL_ARCHIVAL.getPrerequisite());
    }

    /** SIGNED has no flag of its own — contentSigning is what declares it — so the ladder terminates there. */
    @Test
    void theLadderTerminatesAtContentSigning() {
        assertNull(FeatureFlag.CONTENT_SIGNING.getPrerequisite());
    }

    @Test
    void flagsOutsideTheLadderHaveNoPrerequisite() {
        for (FeatureFlag flag : FeatureFlag.values()) {
            if (!LEVEL_RUNGS.contains(flag)) {
                assertNull(flag.getPrerequisite(), flag.name() + " declares a prerequisite it does not need");
            }
        }
    }

    /** The codes are advertised on /v2/info, so a changed one silently stops matching a connector's declaration. */
    @Test
    void levelRungWireCodesArePinned() {
        assertEquals("levelTimestamped", FeatureFlag.LEVEL_TIMESTAMPED.getCode());
        assertEquals("levelLongTerm", FeatureFlag.LEVEL_LONG_TERM.getCode());
        assertEquals("levelArchival", FeatureFlag.LEVEL_ARCHIVAL.getCode());
        for (FeatureFlag rung : LEVEL_RUNGS) {
            assertSame(rung, FeatureFlag.findByCode(rung.getCode()), rung.name());
        }
    }
}

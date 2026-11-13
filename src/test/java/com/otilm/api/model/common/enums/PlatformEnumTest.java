package com.otilm.api.model.common.enums;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.otilm.api.model.core.acme.AcmeIdentifierAuthorizationMode;
import com.otilm.api.model.core.acme.AcmeIdentifierMatchType;
import com.otilm.api.model.core.cbom.CbomAssetSyncState;
import com.otilm.api.model.core.cbom.CbomIngestFindingKind;
import com.otilm.api.model.core.cbom.CbomSyncSkipState;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetType;
import com.otilm.api.model.core.cryptoasset.PqcExplanationStepOutcome;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import com.otilm.api.model.core.notification.NotificationDataCategory;
import com.otilm.api.model.core.protocol.ProtocolChallengeSource;
import com.otilm.api.model.core.scheduler.ScheduledJobScheduleState;
import io.swagger.v3.core.jackson.TypeNameResolver;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformEnumTest {

    @Test
    void notificationDataCategoryIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(NotificationDataCategory.class);
        assertNotNull(entry);
        assertEquals(NotificationDataCategory.class, entry.getEnumClass());
        assertEquals("NotificationDataCategory", entry.getCode());
    }

    @Test
    void protocolChallengeSourceIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(ProtocolChallengeSource.class);
        assertNotNull(entry);
        assertEquals(PlatformEnum.PROTOCOL_CHALLENGE_SOURCE, entry);
        assertEquals(ProtocolChallengeSource.class, entry.getEnumClass());
        assertEquals("ProtocolChallengeSource", entry.getCode());
    }

    @Test
    void acmeIdentifierEnumsAreRegistered() {
        assertEquals(PlatformEnum.ACME_IDENTIFIER_MATCH_TYPE, PlatformEnum.findByClass(AcmeIdentifierMatchType.class));
        assertEquals("AcmeIdentifierMatchType", PlatformEnum.findByClass(AcmeIdentifierMatchType.class).getCode());
        assertEquals(PlatformEnum.ACME_IDENTIFIER_AUTHORIZATION_MODE,
                PlatformEnum.findByClass(AcmeIdentifierAuthorizationMode.class));
        assertEquals("AcmeIdentifierAuthorizationMode",
                PlatformEnum.findByClass(AcmeIdentifierAuthorizationMode.class).getCode());
    }

    @Test
    void everyEntryHasEnumClassAndLabel() {
        for (PlatformEnum entry : PlatformEnum.values()) {
            assertNotNull(entry.getEnumClass(), "enum class missing for " + entry.name());
            assertTrue(entry.getEnumClass().isEnum(), "not an enum: " + entry.name());
            assertFalse(entry.getLabel().isBlank(), "label missing for " + entry.name());
        }
    }

    @Test
    void registeredEnumClassesAreUnique() {
        Set<Class<?>> seen = new HashSet<>();
        for (PlatformEnum entry : PlatformEnum.values()) {
            assertTrue(seen.add(entry.getEnumClass()),
                    "enum class registered twice: " + entry.getEnumClass().getSimpleName());
        }
    }

    @Test
    void everyKeyIsItsEnumsOpenApiSchemaName() {
        List<String> mismatches = Arrays
                .stream(PlatformEnum.values())
                .filter(entry -> !entry.getCode().equals(openApiSchemaNameOf(entry.getEnumClass())))
                .map(entry -> entry.getCode() + " vs " + openApiSchemaNameOf(entry.getEnumClass()))
                .toList();

        assertEquals(List.of(), mismatches, "keys that differ from their enum's OpenAPI schema name");
    }

    @Test
    void keysAreUnique() {
        Set<String> seen = new HashSet<>();
        for (PlatformEnum entry : PlatformEnum.values()) {
            assertTrue(seen.add(entry.getCode()), "key registered twice: " + entry.getCode());
        }
    }

    @Test
    void cryptographicAssetTypeIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(CryptographicAssetType.class);
        assertNotNull(entry);
        assertEquals(CryptographicAssetType.class, entry.getEnumClass());
        assertEquals("CryptographicAssetType", entry.getCode());
    }

    @Test
    void pqcVerdictIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(PqcVerdict.class);
        assertNotNull(entry);
        assertEquals(PqcVerdict.class, entry.getEnumClass());
        assertEquals("PqcVerdict", entry.getCode());
    }

    @Test
    void pqcExplanationStepOutcomeIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(PqcExplanationStepOutcome.class);
        assertNotNull(entry);
        assertEquals(PqcExplanationStepOutcome.class, entry.getEnumClass());
        assertEquals("PqcExplanationStepOutcome", entry.getCode());
    }

    @Test
    void cbomAssetSyncStateIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(CbomAssetSyncState.class);
        assertNotNull(entry);
        assertEquals(CbomAssetSyncState.class, entry.getEnumClass());
        assertEquals("CbomAssetSyncState", entry.getCode());
    }

    @Test
    void cbomSyncSkipStateIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(CbomSyncSkipState.class);
        assertNotNull(entry);
        assertEquals(CbomSyncSkipState.class, entry.getEnumClass());
        assertEquals("CbomSyncSkipState", entry.getCode());
    }

    @Test
    void cbomIngestFindingKindIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(CbomIngestFindingKind.class);
        assertNotNull(entry);
        assertEquals(CbomIngestFindingKind.class, entry.getEnumClass());
        assertEquals("CbomIngestFindingKind", entry.getCode());
    }

    @Test
    void scheduledJobScheduleStateIsRegistered() {
        PlatformEnum entry = PlatformEnum.findByClass(ScheduledJobScheduleState.class);
        assertEquals(PlatformEnum.SCHEDULED_JOB_SCHEDULE_STATE, entry);
        assertEquals(ScheduledJobScheduleState.class, entry.getEnumClass());
        assertEquals("ScheduledJobScheduleState", entry.getCode());
    }

    private static String openApiSchemaNameOf(Class<?> enumClass) {
        return TypeNameResolver.std.nameForType(TypeFactory.defaultInstance().constructType(enumClass));
    }
}

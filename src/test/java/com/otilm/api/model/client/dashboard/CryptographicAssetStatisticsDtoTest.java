package com.otilm.api.model.client.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Round-trips the dashboard payload so the wire shape — field names and the nested sync-completeness block — fails a
 * build when it changes.
 */
class CryptographicAssetStatisticsDtoTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void roundTripsThroughJson() throws Exception {
        CryptographicAssetStatisticsDto dto = new CryptographicAssetStatisticsDto();
        dto.setTotalAssets(2129L);
        dto.setSourceCbomCount(11L);
        dto
                .setStatByType(Map
                        .of("algorithm", 1500L, "certificate", 400L, "protocol", 120L, "related-crypto-material",
                                105L));
        dto.setUntypedAssetCount(4L);
        dto.setStatByPqcVerdict(Map.of("ready", 300L, "notReady", 1600L, "notApplicable", 100L, "unknown", 129L));
        dto.setStatByAlgorithmFamily(Map.of("AES", 700L, "RSA", 500L));
        dto.setDistinctAlgorithmFamilyCount(41L);
        dto.setUnassignedAssetCount(300L);

        CryptographicAssetSyncCompletenessDto sync = new CryptographicAssetSyncCompletenessDto();
        sync.setCbomStatBySyncState(Map.of("pending", 1L, "inProgress", 0L, "synced", 12L, "failed", 1L));
        sync.setLastCompletedSyncAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        dto.setSyncCompleteness(sync);

        String json = mapper.writeValueAsString(dto);
        Assertions.assertEquals(dto, mapper.readValue(json, CryptographicAssetStatisticsDto.class));

        JsonNode tree = mapper.readTree(json);
        Assertions.assertEquals(2129L, tree.get("totalAssets").asLong());
        Assertions.assertEquals(1600L, tree.get("statByPqcVerdict").get("notReady").asLong());
        Assertions.assertEquals(4L, tree.get("untypedAssetCount").asLong());
        Assertions.assertEquals(12L, tree.get("syncCompleteness").get("cbomStatBySyncState").get("synced").asLong());
    }

    @Test
    void absentLastCompletedSyncAtIsOmittedFromJson() throws Exception {
        CryptographicAssetSyncCompletenessDto sync = new CryptographicAssetSyncCompletenessDto();
        sync.setCbomStatBySyncState(Map.of("pending", 1L, "inProgress", 0L, "synced", 0L, "failed", 0L));
        JsonNode json = mapper.readTree(mapper.writeValueAsString(sync));
        Assertions.assertFalse(json.has("lastCompletedSyncAt"), "absent timestamp must be omitted, not null");
    }
}

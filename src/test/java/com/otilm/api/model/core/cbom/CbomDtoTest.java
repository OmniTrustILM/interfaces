package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;

/**
 * Pins the additive asset-sync fields on the CBOM row: they serialize under the documented names and stay absent-safe,
 * so a Core that has never run an asset sync still produces a valid row.
 */
class CbomDtoTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void assetSyncFieldsRoundTripThroughJson() throws Exception {
        CbomDto dto = new CbomDto();
        dto.setUuid(UUID.fromString("00000000-0000-4000-8000-000000000299"));
        dto.setSerialNumber("urn:uuid:11111111-2222-3333-4444-555555555555");
        dto.setVersion(3);
        dto.setAssetSyncState(CbomAssetSyncState.FAILED);
        dto.setAssetSyncedAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        dto.setAssetSyncError("The document repeats a bom-ref 3 times; CycloneDX requires it to be unique");

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertEquals("failed", json.get("assetSyncState").asText());
        Assertions.assertFalse(json.get("assetSyncedAt").isNull());
        Assertions
                .assertEquals("The document repeats a bom-ref 3 times; CycloneDX requires it to be unique",
                        json.get("assetSyncError").asText());
        Assertions.assertEquals(dto, mapper.readValue(json.toString(), CbomDto.class));
    }

    @Test
    void assetSyncFieldsAreOptional() throws Exception {
        CbomDto back = mapper.readValue("{\"serialNumber\":\"urn:x\",\"version\":1}", CbomDto.class);
        Assertions.assertNull(back.getAssetSyncState());
        Assertions.assertNull(back.getAssetSyncedAt());
        Assertions.assertNull(back.getAssetSyncError());
    }

    @Test
    void absentAssetSyncFieldsAreOmittedFromJson() throws Exception {
        CbomDto dto = new CbomDto();
        dto.setSerialNumber("urn:x");
        dto.setVersion(1);
        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertFalse(json.has("assetSyncState"), "absent sync state must be omitted, not null");
        Assertions.assertFalse(json.has("assetSyncedAt"), "absent sync time must be omitted, not null");
        Assertions.assertFalse(json.has("assetSyncError"), "a record with nothing to report must omit the reason");
    }

    /** A synced record carries no reason: the member is omitted, not null, whatever the state says. */
    @Test
    void aSyncedRecordOmitsTheReason() throws Exception {
        CbomDto dto = new CbomDto();
        dto.setSerialNumber("urn:x");
        dto.setVersion(1);
        dto.setAssetSyncState(CbomAssetSyncState.SYNCED);
        dto.setAssetSyncedAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertEquals("synced", json.get("assetSyncState").asText());
        Assertions.assertFalse(json.has("assetSyncError"), "a synced record must omit the reason, not send null");
    }

    /** The reason is optional in the schema and reaches the detail, on the wire and in the published schema. */
    @Test
    void theAssetSyncReasonIsOptionalAndReachesTheDetail() throws Exception {
        Schema cbom = openApi31Schemas(CbomDto.class).get(CbomDto.class.getSimpleName());
        Assertions.assertNotNull(cbom);
        Assertions.assertNotNull(cbom.getProperties().get("assetSyncError"), "assetSyncError must be a row property");
        Assertions
                .assertFalse(cbom.getRequired() != null && cbom.getRequired().contains("assetSyncError"),
                        "assetSyncError is absent while there is no failure to report");

        CbomDetailDto detail = new CbomDetailDto();
        detail.setSerialNumber("urn:x");
        detail.setVersion(1);
        detail.setAssetSyncError("reason");
        detail.setContent(Map.of());
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));
        Assertions.assertEquals("reason", json.get("assetSyncError").asText());

        Map<String, Schema> detailSchemas = openApi31Schemas(CbomDetailDto.class);
        Schema detailSchema = detailSchemas.get(CbomDetailDto.class.getSimpleName());
        Assertions.assertNotNull(detailSchema);
        boolean direct = detailSchema.getProperties() != null
                && detailSchema.getProperties().containsKey("assetSyncError");
        boolean composed = detailSchema.getAllOf() != null && detailSchema.getAllOf().stream().anyMatch(part -> {
            Schema<?> arm = (Schema<?>) part;
            if (arm.get$ref() != null) {
                Schema referenced = detailSchemas.get(arm.get$ref().substring(arm.get$ref().lastIndexOf('/') + 1));
                return referenced != null && referenced.getProperties() != null
                        && referenced.getProperties().containsKey("assetSyncError");
            }
            return arm.getProperties() != null && arm.getProperties().containsKey("assetSyncError");
        });
        Assertions.assertTrue(direct || composed, "the published detail schema must carry assetSyncError");
    }
}

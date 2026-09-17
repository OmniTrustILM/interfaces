package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;

/**
 * One row of the documents the CBOM sync could not store: every member is always present, because the row is written
 * whole on the first failure and only ever updated in place.
 */
class CbomSyncSkipDtoTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void everyMemberIsRequired() {
        Schema row = openApi31Schemas(CbomSyncSkipDto.class).get(CbomSyncSkipDto.class.getSimpleName());
        Assertions.assertNotNull(row);
        List<String> expected = List
                .of("uuid", "serialNumber", "version", "state", "attempts", "firstSkippedAt", "lastAttemptAt", "reason",
                        "algorithms", "certificates", "protocols", "cryptoMaterial", "totalAssets");
        Assertions.assertEquals(expected.stream().sorted().toList(), row.getRequired().stream().sorted().toList());
        Assertions.assertEquals(expected.size(), row.getProperties().size(), "no member beyond the documented ones");
    }

    @Test
    void roundTripsThroughJsonWithTheStateAsItsWireCode() throws Exception {
        CbomSyncSkipDto dto = new CbomSyncSkipDto();
        dto.setUuid(UUID.fromString("00000000-0000-4000-8000-000000002250"));
        dto.setSerialNumber("urn:uuid:11111111-2222-3333-4444-555555555555");
        dto.setVersion(2);
        dto.setState(CbomSyncSkipState.PERMANENTLY_SKIPPED);
        dto.setAttempts(4);
        dto.setFirstSkippedAt(OffsetDateTime.of(2026, 9, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        dto.setLastAttemptAt(OffsetDateTime.of(2026, 9, 4, 12, 0, 0, 0, ZoneOffset.UTC));
        dto.setReason("The CBOM Repository answered 404 for the document");
        dto.setAlgorithms(3);
        dto.setCertificates(2);
        dto.setProtocols(1);
        dto.setCryptoMaterial(0);
        dto.setTotalAssets(6);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertEquals("permanentlySkipped", json.get("state").asText());
        Assertions.assertEquals(4, json.get("attempts").asInt());
        Assertions.assertEquals(dto, mapper.readValue(json.toString(), CbomSyncSkipDto.class));
    }
}

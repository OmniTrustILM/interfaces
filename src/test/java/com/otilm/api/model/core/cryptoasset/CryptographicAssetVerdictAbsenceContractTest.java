package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The verdict provenance block is served only once a rule set has evaluated the asset, and core keeps a row without it
 * reachable by design: a row waits between its upsert and its first evaluation, and a row whose first verdict write
 * failed or was refused by the row-version guard stays on the sweep's work list until the next run (a failed
 * evaluation, by contrast, is stamped UNKNOWN / EVALUATION-FAILED and does carry a block). The contract therefore has
 * to say the block can be absent — the schema must not require it, and the wire omits it rather than sending null. The
 * row-level {@code pqcVerdict} is the member that always carries a value.
 */
class CryptographicAssetVerdictAbsenceContractTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void theDetailSchemaDoesNotRequireTheVerdictBlock() {
        Schema<?> detail = readAll(CryptographicAssetDetailDto.class)
                .get(CryptographicAssetDetailDto.class.getSimpleName());
        assertNotNull(detail, "the detail schema must resolve");
        List<String> required = detail.getRequired();
        assertNotNull(required, "the detail schema still requires its always-present members");
        assertFalse(required.contains("verdict"),
                "verdict is absent until the first evaluation and must not be required, got " + required);
        assertTrue(required.containsAll(List.of("sources", "oids")),
                "the always-present detail members stay required, got " + required);
    }

    @Test
    void aNeverEvaluatedDetailSerializesWithoutTheVerdictBlock() throws Exception {
        CryptographicAssetDetailDto detail = new CryptographicAssetDetailDto();
        detail.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000000938"));
        detail.setName("RSA-2048");
        detail.setType(CryptographicAssetType.ALGORITHM);
        detail.setPqcVerdict(PqcVerdict.UNKNOWN);
        detail.setSources(List.of());
        detail.setOids(List.of());

        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        assertFalse(json.has("verdict"), "a never-evaluated asset carries no provenance block, not a null one");
        assertEquals("unknown", json.get("pqcVerdict").asText(), "the row-level verdict still carries a value");
    }

    /** {@code ModelConverters.readAll} is raw; the cast is contained here. */
    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }
}

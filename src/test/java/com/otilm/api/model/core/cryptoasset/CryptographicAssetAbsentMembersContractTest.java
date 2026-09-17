package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Two members of the inventory surface are absent by design, and the contract has to say so. A row whose producers
 * recorded no name, and whose recorded OID may not stand in for one because it is refuted, has no {@code name}. A
 * source CBOM whose metadata component carries no name has no {@code source}. Core's wire mapper omits nulls, so both
 * are absent on the wire rather than null: the schema must not require them, and the DTOs must omit them when
 * serialized on their own, the way the detail already omits its optional members.
 */
class CryptographicAssetAbsentMembersContractTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void theRowSchemaDoesNotRequireTheName() {
        List<String> required = requiredOf(CryptographicAssetDto.class);
        assertFalse(required.contains("name"), "name is absent for a nameless refuted-OID row, got " + required);
        assertTrue(
                required
                        .containsAll(List
                                .of("uuid", "type", "pqcVerdict", "sourceCbomCount", "occurrenceCount", "quarantined")),
                "the always-present row members stay required, got " + required);
    }

    @Test
    void theSourceSchemaDoesNotRequireTheSource() {
        List<String> required = requiredOf(CryptographicAssetSourceDto.class);
        assertFalse(required.contains("source"),
                "source is absent when the CBOM's metadata component has no name, got " + required);
        assertTrue(required.containsAll(List.of("cbomUuid", "serialNumber", "version", "occurrenceCount")),
                "the always-present source members stay required, got " + required);
    }

    @Test
    void aRowWithoutAServableNameSerializesWithoutTheName() throws Exception {
        CryptographicAssetDto row = new CryptographicAssetDto();
        row.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000000938"));
        row.setType(CryptographicAssetType.ALGORITHM);
        row.setPqcVerdict(PqcVerdict.UNKNOWN);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(row));

        assertFalse(json.has("name"), "a row with no servable name omits the member, it does not send null");
    }

    @Test
    void aSourceWithoutAMetadataComponentNameSerializesWithoutTheSource() throws Exception {
        CryptographicAssetSourceDto source = new CryptographicAssetSourceDto();
        source.setCbomUuid(UUID.fromString("00000000-0000-4000-8000-000000000299"));
        source.setSerialNumber("urn:uuid:11111111-2222-3333-4444-555555555555");
        source.setVersion(3);
        source.setOccurrenceCount(3L);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(source));

        assertFalse(json.has("source"), "a source without a producing tool name omits the member, not null");
    }

    private static List<String> requiredOf(Class<?> type) {
        Schema<?> schema = readAll(type).get(type.getSimpleName());
        assertNotNull(schema, type.getSimpleName() + " schema must resolve");
        List<String> required = schema.getRequired();
        assertNotNull(required, type.getSimpleName() + " still requires its always-present members");
        return required;
    }

    /** {@code ModelConverters.readAll} is raw; the cast is contained here. */
    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }
}

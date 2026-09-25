package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Round-trips the full detail payload so the wire shape — nesting, field names and enum codes — fails a build when it
 * changes, instead of failing the first consumer.
 */
class CryptographicAssetDtoSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private static CryptographicAssetDetailDto sampleDetail() {
        CryptographicAssetDetailDto detail = new CryptographicAssetDetailDto();
        detail.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000000874"));
        detail.setName("AES-128-GCM");
        detail.setType(CryptographicAssetType.ALGORITHM);
        detail.setPqcVerdict(PqcVerdict.NOT_READY);
        detail.setSourceCbomCount(2);
        detail.setSightingCount(5L);

        CryptographicAssetVerdictDto verdict = new CryptographicAssetVerdictDto();
        verdict.setRuleSetVersion(1);
        verdict.setRuleId("pqc-symmetric-key-floor");
        verdict.setReason("Symmetric key length below the post-quantum floor");
        verdict.setEvaluatedFields(Map.of("parameterSet", "128"));
        verdict.setDecidedAt(OffsetDateTime.of(2026, 7, 15, 9, 0, 0, 0, ZoneOffset.UTC));
        verdict.setEvaluatedAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        detail.setVerdict(verdict);

        CryptographicAssetNormalizedFieldsDto normalized = new CryptographicAssetNormalizedFieldsDto();
        normalized.setAlgorithmFamily("AES");
        normalized.setParameterSet("128");
        normalized.setMode("GCM");
        detail.setNormalizedFields(normalized);
        detail.setElectedPayload(Map.of("primitive", "ae", "parameterSetIdentifier", "128"));

        CryptographicAssetEvidenceDto evidence = new CryptographicAssetEvidenceDto();
        evidence.setLocation("src/crypto/cipher.go");
        evidence.setLine(42);
        evidence.setOffset(7);
        evidence.setSymbol("newGCM");

        CryptographicAssetSourceDto source = new CryptographicAssetSourceDto();
        source.setCbomUuid(UUID.fromString("00000000-0000-4000-8000-000000000299"));
        source.setSerialNumber("urn:uuid:11111111-2222-3333-4444-555555555555");
        source.setVersion(3);
        source.setLocationCount(3L);
        source.setSource("CBOM-Lens");
        source.setPayload(Map.of("primitive", "ae", "parameterSetIdentifier", "128"));
        source.setEvidence(List.of(evidence));
        detail.setSources(List.of(source));

        CryptographicAssetOidDto oid = new CryptographicAssetOidDto();
        oid.setOid("2.16.840.1.101.3.4.1.6");
        oid.setRefuted(true);
        detail.setOids(List.of(oid));
        return detail;
    }

    @Test
    void detailRoundTripsThroughJson() throws Exception {
        CryptographicAssetDetailDto detail = sampleDetail();
        String json = mapper.writeValueAsString(detail);
        CryptographicAssetDetailDto back = mapper.readValue(json, CryptographicAssetDetailDto.class);
        Assertions.assertEquals(detail, back);
    }

    @Test
    void detailSerializesTheDocumentedFieldNamesAndEnumCodes() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(sampleDetail()));
        Assertions.assertEquals("AES-128-GCM", json.get("name").asText());
        Assertions.assertEquals("algorithm", json.get("type").asText());
        Assertions.assertEquals("notReady", json.get("pqcVerdict").asText());
        Assertions.assertEquals(2, json.get("sourceCbomCount").asInt());
        Assertions.assertEquals(5, json.get("sightingCount").asLong());
        Assertions.assertFalse(json.get("quarantined").asBoolean());
        Assertions.assertEquals(1, json.get("verdict").get("ruleSetVersion").asInt());
        Assertions.assertEquals("pqc-symmetric-key-floor", json.get("verdict").get("ruleId").asText());
        Assertions.assertEquals("AES", json.get("normalizedFields").get("algorithmFamily").asText());
        Assertions.assertEquals("ae", json.get("electedPayload").get("primitive").asText());
        JsonNode source = json.get("sources").get(0);
        Assertions.assertEquals("urn:uuid:11111111-2222-3333-4444-555555555555", source.get("serialNumber").asText());
        Assertions.assertEquals(3, source.get("locationCount").asLong());
        Assertions.assertEquals("ae", source.get("payload").get("primitive").asText());
        Assertions.assertEquals("src/crypto/cipher.go", source.get("evidence").get(0).get("location").asText());
        JsonNode oid = json.get("oids").get(0);
        Assertions.assertEquals("2.16.840.1.101.3.4.1.6", oid.get("oid").asText());
        Assertions.assertTrue(oid.get("refuted").asBoolean());
    }

    @Test
    void listRowRoundTripsThroughJson() throws Exception {
        CryptographicAssetDto row = new CryptographicAssetDto();
        row.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000000874"));
        row.setName("TLSv1.2");
        row.setType(CryptographicAssetType.PROTOCOL);
        row.setPqcVerdict(PqcVerdict.UNKNOWN);
        row.setSourceCbomCount(1);
        row.setSightingCount(1L);
        String json = mapper.writeValueAsString(row);
        Assertions.assertEquals(row, mapper.readValue(json, CryptographicAssetDto.class));
    }

    @Test
    void aSourceWithoutLocationsStillSerializesItsZeroLocationCount() throws Exception {
        CryptographicAssetSourceDto source = new CryptographicAssetSourceDto();
        source.setCbomUuid(UUID.fromString("00000000-0000-4000-8000-000000002299"));
        source.setSerialNumber("urn:uuid:11111111-2222-3333-4444-555555555555");
        source.setVersion(1);
        source.setEvidence(List.of());

        JsonNode json = mapper.readTree(mapper.writeValueAsString(source));

        Assertions.assertTrue(json.has("locationCount"), "a zero location count is served, not omitted");
        Assertions.assertEquals(0, json.get("locationCount").asLong());
    }

    @Test
    void absentOptionalMembersAreOmittedFromJson() throws Exception {
        CryptographicAssetVerdictDto verdict = new CryptographicAssetVerdictDto();
        verdict.setRuleSetVersion(2);
        verdict.setDecidedAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        verdict.setEvaluatedAt(OffsetDateTime.of(2026, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        JsonNode json = mapper.readTree(mapper.writeValueAsString(verdict));
        Assertions.assertFalse(json.has("ruleId"), "absent ruleId must be omitted, not null");
        Assertions.assertFalse(json.has("reason"), "absent reason must be omitted, not null");
        Assertions.assertFalse(json.has("evaluatedFields"), "absent evaluatedFields must be omitted, not null");
    }
}

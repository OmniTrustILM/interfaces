package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An explanation always carries the recomputed verdict and the rules walked; what was stored is absent for an asset
 * that has not been evaluated yet, and a step that was not reached read nothing and decided nothing.
 */
class CryptographicAssetPqcExplanationContractTest {

    private static final UUID ASSET_UUID = UUID.fromString("d3adbeef-0000-4000-8000-000000002361");

    private static final UUID KEY_UUID = UUID.fromString("d3adbeef-0000-4000-8000-000000002360");

    private static final UUID SIGNATURE_UUID = UUID.fromString("d3adbeef-0000-4000-8000-000000002362");

    private static final OffsetDateTime EXPLAINED_AT = OffsetDateTime.of(2026, 9, 29, 12, 0, 0, 0, ZoneOffset.UTC);

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void theExplanationRequiresItsRecomputedMembersOnly() {
        List<String> required = requiredOf(CryptographicAssetPqcExplanationDto.class);
        assertTrue(required
                .containsAll(List
                        .of("uuid", "verdict", "ruleId", "reason", "inputs", "steps", "matchesStored", "explainedAt")),
                "the recomputed members are always present, got " + required);
        assertFalse(required.contains("storedVerdict"), "absent before the first evaluation, got " + required);
        assertFalse(required.contains("storedRuleId"), "absent before the first evaluation, got " + required);
        assertFalse(required.contains("storedEvaluatedAt"), "absent before the first evaluation, got " + required);
    }

    @Test
    void aStepRequiresItsRuleTitleOutcomeAndMessageOnly() {
        List<String> required = requiredOf(PqcExplanationStepDto.class);
        assertEquals(List.of("message", "outcome", "ruleId", "title"), required.stream().sorted().toList());
    }

    @Test
    void anExplanationRoundTripsThroughJson() throws Exception {
        CryptographicAssetPqcExplanationDto explanation = sampleExplanation();
        explanation.setMatchesStored(true);
        explanation.setStoredVerdict(PqcVerdict.NOT_READY);
        explanation.setStoredRuleId("CERT-SIGNATURE-ALGORITHM");
        explanation.setStoredEvaluatedAt(EXPLAINED_AT.minusHours(1));

        String json = mapper.writeValueAsString(explanation);

        assertEquals(explanation, mapper.readValue(json, CryptographicAssetPqcExplanationDto.class));
        JsonNode steps = mapper.readTree(json).get("steps");
        assertEquals("notMatched", steps.get(0).get("outcome").asText());
        assertEquals("resolved", steps.get(1).get("outcome").asText());
        assertEquals(SIGNATURE_UUID.toString(), steps.get(1).get("referencedAsset").get("uuid").asText());
        assertEquals("sha256withrsa", steps.get(1).get("referencedAsset").get("name").asText());
        assertEquals("algorithm", steps.get(1).get("referencedAsset").get("type").asText());
        assertTrue(steps.get(1).get("referencedAsset").get("visible").asBoolean());
        assertEquals("notReached", steps.get(2).get("outcome").asText());
    }

    @Test
    void aNeverEvaluatedAssetOmitsTheStoredMembers() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(sampleExplanation()));

        assertFalse(json.get("matchesStored").asBoolean());
        assertFalse(json.has("storedVerdict"), "absent storedVerdict must be omitted, not null");
        assertFalse(json.has("storedRuleId"), "absent storedRuleId must be omitted, not null");
        assertFalse(json.has("storedEvaluatedAt"), "absent storedEvaluatedAt must be omitted, not null");
    }

    @Test
    void aStepThatWasNotReachedOmitsWhatItDidNotProduce() throws Exception {
        JsonNode notReached = mapper.readTree(mapper.writeValueAsString(sampleExplanation())).get("steps").get(2);

        assertFalse(notReached.has("verdict"), "a step that was not reached yields no verdict");
        assertFalse(notReached.has("evaluatedFields"), "a step that was not reached read nothing");
        assertFalse(notReached.has("referencedAsset"), "a step that was not reached refers to nothing");
    }

    @Test
    void aReferencedAssetOutOfSightServesItsUuidAlone() throws Exception {
        PqcReferencedAssetDto hidden = new PqcReferencedAssetDto();
        hidden.setUuid(KEY_UUID);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(hidden));

        assertEquals(KEY_UUID.toString(), json.get("uuid").asText());
        assertFalse(json.get("visible").asBoolean(), "a referenced asset out of sight says so");
        assertFalse(json.has("name"), "a referenced asset the reader cannot see omits its name, not null");
        assertFalse(json.has("type"), "a referenced asset the reader cannot see omits its type, not null");
        assertEquals(List.of("uuid", "visible"), requiredOf(PqcReferencedAssetDto.class).stream().sorted().toList());
    }

    @Test
    void theStoredVerdictCarriesTheReferencedAssetOptionally() throws Exception {
        assertFalse(requiredOf(CryptographicAssetVerdictDto.class).contains("referencedAsset"),
                "absent when the asset's own properties decided");

        CryptographicAssetVerdictDto verdict = new CryptographicAssetVerdictDto();
        verdict.setRuleId("CERT-SUBJECT-KEY");
        verdict.setReferencedAsset(referencedKey());
        verdict.setDecidedAt(EXPLAINED_AT);
        verdict.setEvaluatedAt(EXPLAINED_AT);

        String json = mapper.writeValueAsString(verdict);

        assertEquals(verdict, mapper.readValue(json, CryptographicAssetVerdictDto.class));
        assertEquals(KEY_UUID.toString(), mapper.readTree(json).get("referencedAsset").get("uuid").asText());
    }

    /** The one shape an asset the rule set cannot evaluate is explained by. */
    @Test
    void anAssetTheRuleSetCannotEvaluateIsExplainedByOneFailedStep() throws Exception {
        PqcExplanationStepDto failed = step("EVALUATION-FAILED", "Evaluation", PqcExplanationStepOutcome.FAILED,
                "The rule set could not be evaluated against this asset's recorded properties");
        failed.setVerdict(PqcVerdict.UNKNOWN);
        CryptographicAssetPqcExplanationDto explanation = new CryptographicAssetPqcExplanationDto();
        explanation.setUuid(ASSET_UUID);
        explanation.setVerdict(PqcVerdict.UNKNOWN);
        explanation.setRuleId("EVALUATION-FAILED");
        explanation.setReason(failed.getMessage());
        explanation.setInputs(Map.of());
        explanation.setSteps(List.of(failed));
        explanation.setExplainedAt(EXPLAINED_AT);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(explanation));

        assertEquals("failed", json.get("steps").get(0).get("outcome").asText());
        assertEquals("unknown", json.get("steps").get(0).get("verdict").asText());
        assertTrue(json.get("inputs").isEmpty());
    }

    /**
     * swagger-core puts an enum-typed property's description on the enum's shared component when the enum declares
     * none, so one field's conditional wording ended up describing the enum everywhere it is used. Each enum carries
     * its own, and it is what the component says whichever DTO is read.
     */
    @Test
    void anEnumComponentKeepsItsOwnDescriptionWhateverFieldUsesIt() {
        for (Class<?> dto : List
                .of(CryptographicAssetDto.class, CryptographicAssetPqcExplanationDto.class, PqcExplanationStepDto.class,
                        PqcReferencedAssetDto.class)) {
            Map<String, Schema<?>> schemas = readAll(dto);
            assertComponentDescription(schemas, CryptographicAssetType.class, dto);
            assertComponentDescription(schemas, PqcVerdict.class, dto);
            assertComponentDescription(schemas, PqcExplanationStepOutcome.class, dto);
        }
    }

    private static void assertComponentDescription(Map<String, Schema<?>> schemas, Class<?> enumType, Class<?> dto) {
        Schema<?> component = schemas.get(enumType.getSimpleName());
        if (component != null) {
            assertEquals(enumType.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class).description(),
                    component.getDescription(), enumType.getSimpleName() + " as read through " + dto.getSimpleName());
        }
    }

    private static CryptographicAssetPqcExplanationDto sampleExplanation() {
        PqcExplanationStepDto notMatched = step("CERT-SUBJECT-KEY", "Certified key",
                PqcExplanationStepOutcome.NOT_MATCHED, "The signature algorithm is weaker than the certified key");
        notMatched.setEvaluatedFields(Map.of("assetType", "certificate", "subjectPublicKeyRef", "key-rsa"));

        PqcExplanationStepDto resolved = step("CERT-SIGNATURE-ALGORITHM", "Signature algorithm",
                PqcExplanationStepOutcome.RESOLVED, "The certificate is signed with a weaker algorithm");
        resolved.setVerdict(PqcVerdict.NOT_READY);
        resolved.setEvaluatedFields(Map.of("assetType", "certificate", "signatureAlgorithmRef", "alg-sig"));
        resolved.setReferencedAsset(referencedSignatureAlgorithm());

        PqcExplanationStepDto notReached = step("CERT-REFERENCE-UNRESOLVED", "Unresolved certificate reference",
                PqcExplanationStepOutcome.NOT_REACHED, "Not evaluated: an earlier rule decided");
        PqcExplanationStepDto catchAll = step("CERT-NO-KEY-RECORDED", "No certified key recorded",
                PqcExplanationStepOutcome.NOT_REACHED, "Not evaluated: an earlier rule decided");

        CryptographicAssetPqcExplanationDto explanation = new CryptographicAssetPqcExplanationDto();
        explanation.setUuid(ASSET_UUID);
        explanation.setVerdict(PqcVerdict.NOT_READY);
        explanation.setRuleId("CERT-SIGNATURE-ALGORITHM");
        explanation.setReason("The certificate is signed with a weaker algorithm");
        explanation.setInputs(Map.of("assetType", "certificate"));
        explanation.setSteps(List.of(notMatched, resolved, notReached, catchAll));
        explanation.setExplainedAt(EXPLAINED_AT);
        return explanation;
    }

    /** What a resolved signature-algorithm step carries: the algorithm asset whose verdict it took. */
    private static PqcReferencedAssetDto referencedSignatureAlgorithm() {
        PqcReferencedAssetDto algorithm = new PqcReferencedAssetDto();
        algorithm.setUuid(SIGNATURE_UUID);
        algorithm.setVisible(true);
        algorithm.setName("sha256withrsa");
        algorithm.setType(CryptographicAssetType.ALGORITHM);
        return algorithm;
    }

    private static PqcReferencedAssetDto referencedKey() {
        PqcReferencedAssetDto key = new PqcReferencedAssetDto();
        key.setUuid(KEY_UUID);
        key.setVisible(true);
        key.setName("ml-kem");
        key.setType(CryptographicAssetType.RELATED_CRYPTO_MATERIAL);
        return key;
    }

    private static PqcExplanationStepDto step(String ruleId, String title, PqcExplanationStepOutcome outcome,
            String message) {
        PqcExplanationStepDto step = new PqcExplanationStepDto();
        step.setRuleId(ruleId);
        step.setTitle(title);
        step.setOutcome(outcome);
        step.setMessage(message);
        return step;
    }

    private static List<String> requiredOf(Class<?> type) {
        Schema<?> schema = readAll(type).get(type.getSimpleName());
        assertNotNull(schema, type.getSimpleName() + " schema must resolve");
        List<String> required = schema.getRequired();
        assertNotNull(required, type.getSimpleName() + " requires its always-present members");
        return required;
    }

    /** {@code ModelConverters.readAll} is raw; the cast is contained here. */
    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }
}

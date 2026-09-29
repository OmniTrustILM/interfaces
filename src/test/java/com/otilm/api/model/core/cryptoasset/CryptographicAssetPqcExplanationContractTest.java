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
    void aStepRequiresItsRuleOutcomeAndMessageOnly() {
        List<String> required = requiredOf(PqcExplanationStepDto.class);
        assertEquals(List.of("message", "outcome", "ruleId"), required.stream().sorted().toList());
    }

    @Test
    void anExplanationRoundTripsThroughJson() throws Exception {
        CryptographicAssetPqcExplanationDto explanation = sampleExplanation();
        explanation.setMatchesStored(true);
        explanation.setStoredVerdict(PqcVerdict.NOT_READY);
        explanation.setStoredRuleId("CERT-SUBJECT-KEY");
        explanation.setStoredEvaluatedAt(EXPLAINED_AT.minusHours(1));

        String json = mapper.writeValueAsString(explanation);

        assertEquals(explanation, mapper.readValue(json, CryptographicAssetPqcExplanationDto.class));
        JsonNode steps = mapper.readTree(json).get("steps");
        assertEquals("notMatched", steps.get(0).get("outcome").asText());
        assertEquals("resolved", steps.get(1).get("outcome").asText());
        assertEquals(KEY_UUID.toString(), steps.get(1).get("referencedAssetUuid").asText());
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
        assertFalse(notReached.has("referencedAssetUuid"), "a step that was not reached refers to nothing");
    }

    private static CryptographicAssetPqcExplanationDto sampleExplanation() {
        PqcExplanationStepDto notMatched = step("PROTOCOL-CIPHER-SUITE", PqcExplanationStepOutcome.NOT_MATCHED,
                "The asset is not a protocol");
        notMatched.setEvaluatedFields(Map.of("assetType", "certificate"));

        PqcExplanationStepDto resolved = step("CERT-SUBJECT-KEY", PqcExplanationStepOutcome.RESOLVED,
                "The certified key is not PQC ready");
        resolved.setVerdict(PqcVerdict.NOT_READY);
        resolved.setEvaluatedFields(Map.of("assetType", "certificate"));
        resolved.setReferencedAssetUuid(KEY_UUID);

        PqcExplanationStepDto notReached = step("CERT-NO-KEY-RECORDED", PqcExplanationStepOutcome.NOT_REACHED,
                "An earlier rule decided");

        CryptographicAssetPqcExplanationDto explanation = new CryptographicAssetPqcExplanationDto();
        explanation.setUuid(ASSET_UUID);
        explanation.setVerdict(PqcVerdict.NOT_READY);
        explanation.setRuleId("CERT-SUBJECT-KEY");
        explanation.setReason("The certified key is not PQC ready");
        explanation.setInputs(Map.of("assetType", "certificate"));
        explanation.setSteps(List.of(notMatched, resolved, notReached));
        explanation.setExplainedAt(EXPLAINED_AT);
        return explanation;
    }

    private static PqcExplanationStepDto step(String ruleId, PqcExplanationStepOutcome outcome, String message) {
        PqcExplanationStepDto step = new PqcExplanationStepDto();
        step.setRuleId(ruleId);
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

package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetType;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryptoAssetPqcNotReadyEventDataTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsThePqcFailureAndPreviousVerdict() throws Exception {
        CryptoAssetPqcNotReadyEventData event = event();
        event.setAssetType(CryptographicAssetType.ALGORITHM);
        event.setName("RSA signing");
        event.setAlgorithmFamily("RSA");
        event.setPreviousVerdict(PqcVerdict.READY);

        String payload = mapper.writeValueAsString(event);
        JsonNode json = mapper.readTree(payload);

        assertEquals(Set
                .of("cryptoAssetUuid", "assetType", "name", "algorithmFamily", "pqcRuleId", "pqcReason",
                        "previousVerdict"),
                fieldNames(json));
        assertEquals("01234567-89ab-cdef-0123-456789abcdef", json.path("cryptoAssetUuid").asText());
        assertEquals("algorithm", json.path("assetType").asText());
        assertEquals("RSA signing", json.path("name").asText());
        assertEquals("RSA", json.path("algorithmFamily").asText());
        assertEquals("ALG-CLASSICAL", json.path("pqcRuleId").asText());
        assertEquals("RSA relies on classical cryptography", json.path("pqcReason").asText());
        assertEquals("ready", json.path("previousVerdict").asText());
        assertEquals(event, mapper.readValue(payload, CryptoAssetPqcNotReadyEventData.class));
    }

    @Test
    void theFirstAssessmentOmitsUnavailableOptionalMembers() throws Exception {
        CryptoAssetPqcNotReadyEventData event = new CryptoAssetPqcNotReadyEventData();
        event.setCryptoAssetUuid(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
        JsonNode json = mapper.readTree(mapper.writeValueAsString(event));

        assertEquals(Set.of("cryptoAssetUuid"), fieldNames(json));
        CryptoAssetPqcNotReadyEventData restored = mapper.treeToValue(json, CryptoAssetPqcNotReadyEventData.class);
        assertNull(restored.getAssetType());
        assertNull(restored.getPqcRuleId());
        assertNull(restored.getPqcReason());
        assertNull(restored.getPreviousVerdict());
        assertTrue(VALIDATORS.validator().validate(restored).isEmpty());
    }

    @Test
    void aNullPreviousVerdictIsReadAsNoEarlierDecidedVerdict() throws Exception {
        String payload = """
                {
                  "cryptoAssetUuid": "01234567-89ab-cdef-0123-456789abcdef",
                  "pqcRuleId": "ALG-CLASSICAL",
                  "pqcReason": "RSA relies on classical cryptography",
                  "previousVerdict": null
                }
                """;

        CryptoAssetPqcNotReadyEventData event = mapper.readValue(payload, CryptoAssetPqcNotReadyEventData.class);

        assertNull(event.getPreviousVerdict());
        assertEquals(Set.of("cryptoAssetUuid", "pqcRuleId", "pqcReason"),
                fieldNames(mapper.readTree(mapper.writeValueAsString(event))));
    }

    @Test
    void thePublishedSchemaRequiresTheAssetAndAllowsAbsentPqcDetails() {
        var schema = OpenApiSchemaTestSupport.openApi31Schema(CryptoAssetPqcNotReadyEventData.class);

        assertEquals(Set.of("cryptoAssetUuid"), new HashSet<>(schema.getRequired()));
        assertEquals(Set
                .of("cryptoAssetUuid", "assetType", "name", "algorithmFamily", "pqcRuleId", "pqcReason",
                        "previousVerdict"),
                schema.getProperties().keySet());
    }

    @Test
    void anEventWithoutAssetIdentityFailsValidation() {
        var violations = VALIDATORS.validator().validate(new CryptoAssetPqcNotReadyEventData());

        assertEquals(Set.of("cryptoAssetUuid"),
                violations
                        .stream()
                        .map(violation -> violation.getPropertyPath().toString())
                        .collect(Collectors.toSet()));
    }

    private static CryptoAssetPqcNotReadyEventData event() {
        CryptoAssetPqcNotReadyEventData event = new CryptoAssetPqcNotReadyEventData();
        event.setCryptoAssetUuid(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
        event.setPqcRuleId("ALG-CLASSICAL");
        event.setPqcReason("RSA relies on classical cryptography");
        return event;
    }

    private static Set<String> fieldNames(JsonNode json) {
        Set<String> fields = new HashSet<>();
        json.fieldNames().forEachRemaining(fields::add);
        return fields;
    }
}

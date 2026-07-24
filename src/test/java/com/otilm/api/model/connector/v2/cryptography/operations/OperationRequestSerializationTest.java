package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureRequestDataV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.otilm.api.model.connector.cryptography.v2.MetadataTestUtils.stringMetadata;
import static org.junit.jupiter.api.Assertions.*;

class OperationRequestSerializationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void signRequestRequiresAUsableKeyHandleAndPreservesEmptyData() throws Exception {
        SignDataRequestV2Dto request = new SignDataRequestV2Dto();
        request.setTokenAttributes(List.of());
        request.setTokenProfileAttributes(List.of());
        request.setKeyMeta(List.of(stringMetadata("keyId", "key-123")));
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        request.setKeyUsages(Set.of(KeyUsage.SIGN));
        request.setSignatureAttributes(List.of());
        request.setData(List.of(new SignatureRequestDataV2Dto(new byte[0], "item-0")));

        JsonNode json = MAPPER.readTree(MAPPER.writeValueAsBytes(request));
        assertTrue(json.get("tokenAttributes").isArray());
        assertTrue(json.get("tokenProfileAttributes").isArray());
        assertTrue(json.get("keyMeta").isArray());
        assertTrue(json.get("signatureAttributes").isArray());
        assertEquals("", json.get("data").get(0).get("data").asText());

        SignDataRequestV2Dto roundTrip = MAPPER.treeToValue(json, SignDataRequestV2Dto.class);
        assertEquals(0, VALIDATOR.validate(roundTrip).size());
        assertEquals(0, roundTrip.getData().get(0).getData().length);
    }

    @Test
    void randomRequestPreservesRequiredEmptyScopeCollections() throws Exception {
        RandomDataRequestV2Dto request = new RandomDataRequestV2Dto();
        request.setTokenAttributes(List.of());
        request.setTokenProfileAttributes(List.of());
        request.setKeyUsages(Set.of(KeyUsage.SIGN));
        request.setLength(32);

        JsonNode json = MAPPER.readTree(MAPPER.writeValueAsBytes(request));
        assertTrue(json.get("tokenAttributes").isArray());
        assertTrue(json.get("tokenProfileAttributes").isArray());

        RandomDataRequestV2Dto roundTrip = MAPPER.treeToValue(json, RandomDataRequestV2Dto.class);
        assertEquals(0, VALIDATOR.validate(roundTrip).size());
    }

    @Test
    void signOperationRequestRequiresNonEmptyValidTrackingMetadata() {
        SignOperationScopedRequestV2Dto request = new SignOperationScopedRequestV2Dto();

        assertFalse(VALIDATOR.validate(request).stream()
                .filter(violation -> violation.getPropertyPath().toString().equals("signOperationMeta"))
                .toList().isEmpty());

        request.setSignOperationMeta(List.of());
        assertFalse(VALIDATOR.validate(request).stream()
                .filter(violation -> violation.getPropertyPath().toString().equals("signOperationMeta"))
                .toList().isEmpty());

        request.setSignOperationMeta(List.of(stringMetadata("", "operation-123")));
        assertTrue(VALIDATOR.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString()
                        .equals("signOperationMeta[0].name")));

        request.setSignOperationMeta(java.util.Collections.singletonList(null));
        assertTrue(VALIDATOR.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString()
                        .equals("signOperationMeta[0].<list element>")));
    }

}

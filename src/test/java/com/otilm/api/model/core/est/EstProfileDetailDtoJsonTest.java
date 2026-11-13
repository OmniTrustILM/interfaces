package com.otilm.api.model.core.est;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstProfileDetailDtoJsonTest {

    @Test
    void detailSerializesThePolicyCodeAndTheGeneratedUrl() {
        EstProfileDetailDto dto = new EstProfileDetailDto();
        dto.setUuid("7c5a2f4e-2c55-4d5d-9a4f-0b8f3f1b6a10");
        dto.setName("est-devices");
        dto.setAuthenticationPolicy(EstAuthenticationPolicy.VENDOR_MTLS);
        dto.setLabel("est-devices");
        dto.setEstUrl("https://ilm.example.com/.well-known/est/est-devices");
        dto.setRetryAfter(7);

        JsonNode json = new ObjectMapper().valueToTree(dto);

        assertEquals("vendorMtls", json.get("authenticationPolicy").asText());
        assertEquals("https://ilm.example.com/.well-known/est/est-devices", json.get("estUrl").asText());
        assertEquals("est-devices", json.get("label").asText());
        assertTrue(json.has("trustedCaCertificates"));
        assertTrue(json.has("keyRecoveryTokenProfile"));
        assertTrue(json.has("certificateAssociations"));
        assertEquals(7, json.get("retryAfter").asInt());
        assertTrue(json.has("issueWaitTimeout"));
        assertTrue(json.has("serverKeyGenerationTimeout"));
    }
}

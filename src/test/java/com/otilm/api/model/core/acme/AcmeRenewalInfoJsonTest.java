package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeRenewalInfoJsonTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void directoryOmitsRenewalInfoWhenNull() throws Exception {
        Directory directory = new Directory();
        directory.setNewNonce("https://acme.example/new-nonce");

        JsonNode json = mapper.readTree(mapper.writeValueAsString(directory));

        assertFalse(json.has("renewalInfo"));
    }

    @Test
    void directoryCarriesRenewalInfoWhenSet() throws Exception {
        Directory directory = new Directory();
        directory.setRenewalInfo("https://acme.example/renewal-info");

        JsonNode json = mapper.readTree(mapper.writeValueAsString(directory));

        assertEquals("https://acme.example/renewal-info", json.get("renewalInfo").asText());
    }

    @Test
    void orderOmitsReplacesWhenNull() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(new Order()));

        assertFalse(json.has("replaces"));
    }

    @Test
    void orderCarriesReplacesWhenSet() throws Exception {
        Order order = new Order();
        order.setReplaces("aYhba4dGQEHhs3uEe6CuLN4ByNQ.AIdlQyE");

        JsonNode json = mapper.readTree(mapper.writeValueAsString(order));

        assertEquals("aYhba4dGQEHhs3uEe6CuLN4ByNQ.AIdlQyE", json.get("replaces").asText());
    }

    @Test
    void issuanceRequestReadsReplaces() throws Exception {
        CertificateIssuanceRequest request = mapper
                .readValue("{\"identifiers\":[],\"replaces\":\"aYhba4dGQEHhs3uEe6CuLN4ByNQ.AIdlQyE\"}",
                        CertificateIssuanceRequest.class);

        assertEquals("aYhba4dGQEHhs3uEe6CuLN4ByNQ.AIdlQyE", request.getReplaces());
    }

    @Test
    void renewalInfoSerializesWithTheRfcMemberNames() throws Exception {
        RenewalInfo info = new RenewalInfo();
        info.setSuggestedWindow(new RenewalInfo.SuggestedWindow("2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z"));
        info.setExplanationUrl("https://example.com/why");

        JsonNode json = mapper.readTree(mapper.writeValueAsString(info));

        assertEquals("2026-01-01T00:00:00Z", json.at("/suggestedWindow/start").asText());
        assertEquals("2026-01-02T00:00:00Z", json.at("/suggestedWindow/end").asText());
        assertEquals("https://example.com/why", json.get("explanationURL").asText());
        assertFalse(json.has("explanationUrl"));
    }

    @Test
    void renewalInfoOmitsExplanationUrlWhenNull() throws Exception {
        RenewalInfo info = new RenewalInfo();
        info.setSuggestedWindow(new RenewalInfo.SuggestedWindow("2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z"));

        String json = mapper.writeValueAsString(info);

        assertFalse(json.contains("explanation"));
        assertTrue(json.contains("suggestedWindow"));
    }

    @Test
    void alreadyReplacedUsesTheAcmeErrorUrn() {
        assertEquals("urn:ietf:params:acme:error:alreadyReplaced", Problem.ALREADY_REPLACED.getType());
        assertEquals("The certificate has already been replaced", Problem.ALREADY_REPLACED.getDetail());
    }
}

package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtectedResourceMetadataDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private static ProtectedResourceAuthorizationServerDto server(String issuer, String clientId, String resource) {
        ProtectedResourceAuthorizationServerDto server = new ProtectedResourceAuthorizationServerDto();
        server.setIssuer(issuer);
        server.setName("Keycloak");
        server.setClientId(clientId);
        server.setResource(resource);
        server.setScopes(List.of("openid", "profile"));
        return server;
    }

    private static ProtectedResourceMetadataDto metadata(ProtectedResourceAuthorizationServerDto server) {
        ProtectedResourceMetadataDto metadata = new ProtectedResourceMetadataDto();
        metadata.setResource("https://ilm.example.com/api");
        metadata.setResourceName("OmniTrust ILM");
        metadata.setAuthorizationServers(List.of(server.getIssuer()));
        metadata.setBearerMethodsSupported(List.of("header"));
        metadata.setOtilmAuthorizationServers(List.of(server));
        return metadata;
    }

    @Test
    void serializesWithSnakeCaseNamesAndRoundTrips() throws Exception {
        ProtectedResourceMetadataDto metadata = metadata(
                server("https://idp.example.com/realms/ilm", "ilm-app", "https://ilm.example.com/api"));

        String json = mapper.writeValueAsString(metadata);
        JsonNode node = mapper.readTree(json);

        assertEquals("https://ilm.example.com/api", node.get("resource").asText());
        assertEquals("OmniTrust ILM", node.get("resource_name").asText());
        assertEquals("https://idp.example.com/realms/ilm", node.get("authorization_servers").get(0).asText());
        assertEquals("header", node.get("bearer_methods_supported").get(0).asText());
        JsonNode entry = node.get("otilm_authorization_servers").get(0);
        assertEquals("https://idp.example.com/realms/ilm", entry.get("issuer").asText());
        assertEquals("Keycloak", entry.get("name").asText());
        assertEquals("ilm-app", entry.get("client_id").asText());
        assertEquals("https://ilm.example.com/api", entry.get("resource").asText());
        assertEquals(2, entry.get("scopes").size());
        assertFalse(json.contains("resourceName"), json);
        assertFalse(json.contains("clientId"), json);

        assertEquals(metadata, mapper.readValue(json, ProtectedResourceMetadataDto.class));
    }

    @Test
    void omitsUnsetClientIdAndResourceButKeepsEmptyScopes() throws Exception {
        ProtectedResourceAuthorizationServerDto server = server("https://idp.example.com", null, null);
        server.setScopes(List.of());

        JsonNode entry = mapper
                .readTree(mapper.writeValueAsString(metadata(server)))
                .get("otilm_authorization_servers")
                .get(0);

        assertFalse(entry.has("client_id"));
        assertFalse(entry.has("resource"));
        assertTrue(entry.get("scopes").isArray());
        assertEquals(0, entry.get("scopes").size());
    }

    @Test
    void withNoAuthorizationServersBothListsAreEmpty() throws Exception {
        JsonNode node = mapper.readTree(mapper.writeValueAsString(new ProtectedResourceMetadataDto()));

        assertEquals(0, node.get("authorization_servers").size());
        assertEquals(0, node.get("otilm_authorization_servers").size());
    }
}

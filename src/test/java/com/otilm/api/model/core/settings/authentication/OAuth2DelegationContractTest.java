package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth2DelegationContractTest {

    private final ObjectMapper mapper = new ObjectMapper();

    static Stream<Class<? extends OAuth2ProviderSettingsUpdateDto>> providerTypes() {
        return Stream
                .of(OAuth2ProviderSettingsUpdateDto.class, OAuth2ProviderSettingsDto.class,
                        OAuth2ProviderSettingsResponseDto.class);
    }

    @ParameterizedTest
    @MethodSource("providerTypes")
    void aNewProviderRefusesDelegationUntilDelegatesAreConfigured(Class<? extends OAuth2ProviderSettingsUpdateDto> type)
            throws Exception {
        JsonNode json = mapper.valueToTree(type.getConstructor().newInstance());

        assertEquals(mapper.readTree("[]"), json.get("allowedDelegates"));
        assertEquals("client_id", json.path("delegateClaim").asText());
    }

    @ParameterizedTest
    @MethodSource("providerTypes")
    void existingSettingsKeepTheirValuesAndReceiveDelegationDefaults(
            Class<? extends OAuth2ProviderSettingsUpdateDto> type) throws Exception {
        OAuth2ProviderSettingsUpdateDto decoded = mapper.readValue("""
                {"issuerUrl":"https://issuer.example", "clientId":"ilm", "usernameClaim":"sub",
                 "scope":["openid"], "audiences":["ilm-api"], "skew":45, "sessionMaxInactiveInterval":600}
                """, type);
        JsonNode json = mapper.valueToTree(decoded);

        assertEquals("https://issuer.example", json.path("issuerUrl").asText());
        assertEquals("ilm", json.path("clientId").asText());
        assertEquals("sub", json.path("usernameClaim").asText());
        assertEquals(mapper.readTree("[\"openid\"]"), json.get("scope"));
        assertEquals(mapper.readTree("[\"ilm-api\"]"), json.get("audiences"));
        assertEquals(45, json.path("skew").asInt());
        assertEquals(600, json.path("sessionMaxInactiveInterval").asInt());
        assertEquals(mapper.readTree("[]"), json.get("allowedDelegates"));
        assertEquals("client_id", json.path("delegateClaim").asText());
    }

    @ParameterizedTest
    @MethodSource("providerTypes")
    void configuredDelegateIdentifiersAndClaimRoundTripWithoutChangingTheirValues(
            Class<? extends OAuth2ProviderSettingsUpdateDto> type) throws Exception {
        OAuth2ProviderSettingsUpdateDto decoded = mapper.readValue("""
                {"allowedDelegates":["Service-A", "service-a"], "delegateClaim":"azp"}
                """, type);
        JsonNode json = mapper.readTree(mapper.writeValueAsString(decoded));

        assertEquals(mapper.readTree("[\"Service-A\",\"service-a\"]"), json.get("allowedDelegates"));
        assertEquals("azp", json.path("delegateClaim").asText());
    }

    @ParameterizedTest
    @MethodSource("providerTypes")
    void theProviderSchemaPublishesOptionalDelegationSettingsAndTheirDefaults(
            Class<? extends OAuth2ProviderSettingsUpdateDto> type) {
        Schema<?> provider = readAll(type).get(type.getSimpleName());
        assertNotNull(provider);
        Map<String, Schema> properties = provider.getProperties();
        Schema<?> allowedDelegates = properties.get("allowedDelegates");
        Schema<?> delegateClaim = properties.get("delegateClaim");

        assertNotNull(allowedDelegates);
        assertEquals("array", allowedDelegates.getType());
        assertEquals("string", allowedDelegates.getItems().getType());
        assertEquals(List.of(), allowedDelegates.getDefault());
        assertNotNull(delegateClaim);
        assertEquals("string", delegateClaim.getType());
        assertEquals("client_id", delegateClaim.getDefault());
        List<String> required = provider.getRequired() == null ? List.of() : provider.getRequired();
        assertFalse(required.contains("allowedDelegates"));
        assertFalse(required.contains("delegateClaim"));
        List<String> existingRequired = type == OAuth2ProviderSettingsUpdateDto.class
                ? List.of()
                : type == OAuth2ProviderSettingsResponseDto.class ? List.of("jwkSetKeys", "name") : List.of("name");
        assertEquals(existingRequired, required);
        assertTrue(properties
                .keySet()
                .containsAll(List
                        .of("issuerUrl", "clientId", "clientSecret", "authorizationUrl", "tokenUrl", "jwkSetUrl",
                                "jwkSet", "scope", "logoutUrl", "postLogoutUrl", "userInfoUrl", "audiences", "skew",
                                "sessionMaxInactiveInterval", "usernameClaim")));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }
}

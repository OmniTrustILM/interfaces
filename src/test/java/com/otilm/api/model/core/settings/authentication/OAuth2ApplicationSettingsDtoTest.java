package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth2ApplicationSettingsDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void storedSettingsWithoutTheBlockLeaveApplicationsUnset() throws Exception {
        OAuth2ProviderSettingsDto provider = mapper
                .readValue("{\"name\":\"keycloak\",\"issuerUrl\":\"https://idp.example.com\"}",
                        OAuth2ProviderSettingsDto.class);

        assertNull(provider.getApplications());
    }

    @Test
    void newBlockDefaultsToDisabledWithNoScopes() throws Exception {
        OAuth2ApplicationSettingsDto applications = mapper.readValue("{}", OAuth2ApplicationSettingsDto.class);

        assertFalse(applications.isEnabled());
        assertTrue(applications.getScopes().isEmpty());
        assertNull(applications.getName());
        assertNull(applications.getClientId());
        assertNull(applications.getResource());
    }

    @Test
    void blockRoundTripsInsideTheProviderSettings() throws Exception {
        OAuth2ApplicationSettingsDto applications = new OAuth2ApplicationSettingsDto();
        applications.setEnabled(true);
        applications.setName("Company login");
        applications.setClientId("ilm-app");
        applications.setScopes(List.of("openid", "profile"));
        applications.setResource("https://ilm.example.com/api");
        OAuth2ProviderSettingsDto provider = new OAuth2ProviderSettingsDto();
        provider.setName("keycloak");
        provider.setApplications(applications);

        String json = mapper.writeValueAsString(provider);
        OAuth2ProviderSettingsDto decoded = mapper.readValue(json, OAuth2ProviderSettingsDto.class);

        assertTrue(json
                .contains("\"applications\":{\"enabled\":true,\"name\":\"Company login\",\"clientId\":\"ilm-app\","
                        + "\"scopes\":[\"openid\",\"profile\"],\"resource\":\"https://ilm.example.com/api\"}"),
                json);
        assertEquals(applications, decoded.getApplications());
        assertEquals(provider, decoded);
    }
}

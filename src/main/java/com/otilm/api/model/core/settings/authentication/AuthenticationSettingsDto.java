package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.core.settings.SettingsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class AuthenticationSettingsDto implements SettingsDto {

    @NotNull
    @Schema(description = "Disable using localhost user for requests from localhost",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean disableLocalhostUser = false;

    @NotNull
    @Schema(description = "Map of OAuth2 providers settings where key is provider name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("oauth2Providers")
    private Map<String, OAuth2ProviderSettingsDto> oAuth2Providers = new HashMap<>();

    /** Pinned like the field, because Jackson 3 names this getter {@code OAuth2Providers}. */
    @JsonProperty("oauth2Providers")
    public Map<String, OAuth2ProviderSettingsDto> getOAuth2Providers() {
        return oAuth2Providers;
    }
}

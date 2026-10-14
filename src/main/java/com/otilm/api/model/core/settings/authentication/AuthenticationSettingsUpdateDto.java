package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class AuthenticationSettingsUpdateDto {

    @NotNull
    @Schema(description = "Disable using localhost user for requests from localhost",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean disableLocalhostUser = false;

    @Valid
    @Schema(description = "List of OAuth2 providers settings to be registered. If null, keep providers as they are, otherwise replace with providers from list")
    @JsonProperty("oauth2Providers")
    private List<OAuth2ProviderSettingsDto> oAuth2Providers;

    /** Pinned like the field, because Jackson 3 names this getter {@code OAuth2Providers}. */
    @JsonProperty("oauth2Providers")
    public List<OAuth2ProviderSettingsDto> getOAuth2Providers() {
        return oAuth2Providers;
    }
}

package com.otilm.api.model.core.settings.authentication;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OAuth2ProviderSettingsResponseDto extends OAuth2ProviderSettingsUpdateDto {

    @NotNull
    @Schema(description = "Name of OAuth2 Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "List of public keys used by the provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<JwkDto> jwkSetKeys;
}

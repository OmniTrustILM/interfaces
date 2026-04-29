package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder("name")
public class OAuth2ProviderSettingsDto extends OAuth2ProviderSettingsUpdateDto {

    @NotNull
    @Schema(description = "Name of OAuth2 Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

}

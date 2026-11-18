package com.otilm.api.model.core.settings.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * How applications, as opposed to the platform's own browser login, sign in through a provider. Enabled providers are
 * advertised in the OAuth Protected Resource Metadata (RFC 9728) of the API, so a client can discover where to obtain a
 * token for it.
 */
@Data
@Schema(description = "Settings for applications that sign in through this provider to call the API")
public class OAuth2ApplicationSettingsDto implements Serializable {

    @Schema(description = "Whether the provider is advertised to applications as an authorization server for the API. "
            + "Requires the provider's issuer URL.", defaultValue = "false",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean enabled;

    @Schema(description = "Name shown to users of an application. The provider name is used when not set.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Public client ID that applications use with PKCE. Must differ from the provider's client ID.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clientId;

    @Schema(description = "Scopes that applications request from the provider.", defaultValue = "[]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> scopes = new ArrayList<>();

    @Schema(description = "Resource indicator (RFC 8707) that applications send to the provider: an absolute http or "
            + "https URI without credentials or a fragment.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String resource;

}

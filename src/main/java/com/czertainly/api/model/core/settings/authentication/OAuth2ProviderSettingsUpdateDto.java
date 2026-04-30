package com.czertainly.api.model.core.settings.authentication;

import com.czertainly.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class OAuth2ProviderSettingsUpdateDto implements Serializable {

    @URL
    @Schema(description = "URL of issuer issuing authentication tokens. If provided, authentication via JWT token is enabled for this provider.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerUrl;

    @Schema(description = "The client ID used to identify the client application during the authorization process.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clientId;

    @Sensitive
    @Schema(description = "The client secret used by the client application to authenticate with the authorization server.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clientSecret;

    @URL
    @Schema(description = "The URL where the authorization server redirects the user for login and authorization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorizationUrl;

    @URL
    @Schema(description = "The URl used to exchange the authorization code or credentials for an access token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tokenUrl;

    @URL
    @Schema(description = "The URL where the JSON Web Key Set (JWKS) containing the public keys used to verify JWT tokens can be retrieved.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String jwkSetUrl;

    @Schema(description = "Base64 encoded JWK Set, provided in case JWK Set URL is not available", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String jwkSet;

    @Schema(description = " The list of scopes that define the access levels and permissions requested by the client application.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> scope = new ArrayList<>();

    @URL
    @Schema(description = "URL to end session on provider side.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String logoutUrl;

    @URL
    @Schema(description = "URL that user will be redirected after logout from application.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postLogoutUrl;

    @URL
    @Schema(description = "The URL containing information about user.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userInfoUrl;

    @Schema(description = "A list of expected audiences for validating the issued tokens, used to match the intended recipients of the tokens.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> audiences = new ArrayList<>();

    @Schema(description = "The allowed time skew, in seconds, for token validation. This accounts for clock differences between systems. Default value is 30 seconds.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int skew = 30;

    @Schema(description = "Duration, in seconds, after which an inactive user's session will be terminated. Default value is 900 seconds.", defaultValue = "900", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int sessionMaxInactiveInterval = 15 * 60;

    @Schema(description = "The claim name used to extract the username from the token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String usernameClaim;

}

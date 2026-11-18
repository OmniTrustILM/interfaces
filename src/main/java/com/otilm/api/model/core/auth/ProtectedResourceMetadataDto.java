package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * OAuth Protected Resource Metadata (RFC 9728) of the API. {@code otilm_authorization_servers} extends the standard
 * document with what an application needs to sign in through each listed authorization server.
 */
@Data
@Schema(description = "OAuth Protected Resource Metadata (RFC 9728) of the API")
public class ProtectedResourceMetadataDto {

    @JsonProperty("resource")
    @Schema(description = "Resource identifier of the API", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resource;

    @JsonProperty("resource_name")
    @Schema(description = "Name of the API", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resourceName;

    @JsonProperty("authorization_servers")
    @Schema(description = "Issuer identifiers of the authorization servers that applications can use",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> authorizationServers = new ArrayList<>();

    @JsonProperty("bearer_methods_supported")
    @Schema(description = "Ways of sending a bearer token to the API", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> bearerMethodsSupported = new ArrayList<>();

    @JsonProperty("otilm_authorization_servers")
    @Schema(description = "Sign-in details for each authorization server in authorization_servers",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProtectedResourceAuthorizationServerDto> otilmAuthorizationServers = new ArrayList<>();

}

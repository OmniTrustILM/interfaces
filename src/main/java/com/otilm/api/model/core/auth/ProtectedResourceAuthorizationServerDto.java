package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Authorization server that applications can use to obtain a token for the API")
public class ProtectedResourceAuthorizationServerDto {

    @JsonProperty("issuer")
    @Schema(description = "Issuer identifier of the authorization server", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuer;

    @JsonProperty("name")
    @Schema(description = "Name to show to users", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @JsonProperty("client_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Public client ID to use with PKCE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clientId;

    @JsonProperty("resource")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Resource indicator (RFC 8707) to send to the authorization server",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String resource;

    @JsonProperty("scopes")
    @Schema(description = "Scopes to request", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> scopes = new ArrayList<>();

}

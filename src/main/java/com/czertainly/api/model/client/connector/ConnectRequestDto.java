package com.czertainly.api.model.client.connector;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.core.connector.AuthType;
import com.czertainly.api.model.core.proxy.ProxyDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
public class ConnectRequestDto {

    @NotNull
    @URL
    @Schema(description = "URL of the Connector to connect",
            examples = {"http://network-discovery-provicer:8080"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "UUID of the Connector. Mandatory if connection is needed for the same Connector")
    private String uuid;

    @NotNull
    @Schema(description = "Type of authentication for the Connector",
            examples = {"none"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AuthType authType;

    @Schema(description = "List of authentication Attributes. Required if the authentication type is not NONE",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> authAttributes;

    @Schema(description = "Proxy for message queue routing. " +
        "When set, connector communicates via message queue proxy. " +
        "When null, connector uses direct REST communication.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProxyDto proxy;

    @AssertTrue(message = "Authentication Attributes must be provided when Authentication Type is not NONE")
    @JsonIgnore
    public boolean isValid() {
        return authType == AuthType.NONE || (authAttributes != null && !authAttributes.isEmpty());
    }
}

package com.czertainly.api.model.core.connector;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.proxy.ProxyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Lightweight connector DTO carrying only the fields required by API clients.
 */
@Setter
@Getter
@ToString(callSuper = true)
public class ConnectorApiClientDtoV1 extends NameAndUuidDto implements ApiClientConnectorInfo {
    @Schema(description = "URL of the Connector",
            examples = {"http://network-discovery-provider:8080"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Type of authentication for the Connector",
            examples = {"none"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AuthType authType;

    @Schema(description = "List of Attributes for the authentication type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> authAttributes;

    @Schema(description = "Status of the Connector",
            examples = {"CONNECTED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorStatus status;

    @Schema(description = "Proxy for message queue routing. " +
            "When set, connector communicates via message queue proxy. " +
            "When null, connector uses direct REST communication.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProxyDto proxy;
}

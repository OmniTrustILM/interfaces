package com.otilm.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Connector registration request sent from proxy to core via message queue.
 * Mirrors the fields of ConnectorRequestDto used in the REST API.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectorRegistrationRequest implements Serializable {

    @Schema(description = "Name of the Connector",
            examples = {"Connector1"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "URL of the Connector to connect",
            examples = {"http://network-discovery-provider:8080"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Type of authentication for the Connector",
            examples = {"none", "basic", "certificate", "apiKey"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String authType;

    @ToString.Exclude
    @Schema(description = "List of authentication Attributes")
    private List<Object> authAttributes;

    @Schema(description = "List of Custom Attributes")
    private List<Object> customAttributes;

    @Schema(description = "Proxy code identifying the proxy instance that forwarded this request",
            examples = {"proxy-001"})
    private String proxyCode;
}

package com.otilm.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * HTTP request details for proxy to forward to the connector.
 * This structure is passed within CoreMessage to specify the actual HTTP call.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectorRequest implements Serializable {

    @Schema(description = "Base URL of the connector",
            examples = {"https://connector.example.com"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUrl;

    @Schema(description = "HTTP method",
            examples = {"GET", "POST", "PUT", "DELETE", "PATCH"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String method;

    @Schema(description = "Request path (relative to connectorUrl)",
            examples = {"/v1/health", "/v1/authorityProvider/authorities/{uuid}"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;

    @Schema(description = "Authentication configuration for the connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorAuth connectorAuth;

    @Schema(description = "Path variables for URL substitution. " +
            "Variables in path like {uuid} will be replaced with values from this map")
    private Map<String, String> pathVariables;

    @Schema(description = "Additional HTTP headers to include in the request")
    private Map<String, String> headers;

    @ToString.Exclude
    @Schema(description = "Request body (JSON object)")
    private Object body;

    @Schema(description = "Request timeout in Go duration format",
            examples = {"30s", "1m", "500ms"},
            defaultValue = "30s")
    private String timeout;

    @Schema(description = "Retry policy configuration")
    private RetryPolicy retryPolicy;

    @ToString.Exclude
    @Schema(description = "Credential data to merge into request body under 'credential' key")
    private Map<String, Object> credentialData;

    /**
     * Retry policy for transient failures.
     */
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RetryPolicy implements Serializable {

        @Schema(description = "Enable retry on transient failures", defaultValue = "false")
        private boolean enabled;

        @Schema(description = "Maximum number of retry attempts", defaultValue = "1")
        private int maxRetries;

        @Schema(description = "Conditions that trigger retry",
                examples = {"5xx", "timeout", "connection_error"})
        private String[] retryOn;
    }

}

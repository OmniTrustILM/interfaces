package com.czertainly.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * HTTP response details from the connector.
 * This structure is returned within ProxyMessage to provide the actual HTTP response data.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectorResponse implements Serializable {

    @Schema(description = "HTTP status code from the connector response. " +
            "0 if request failed before reaching connector",
            examples = {"200", "404", "500", "0"})
    private int statusCode;

    @Schema(description = "HTTP response headers from the connector")
    private Map<String, String> headers;

    @Schema(description = "Response body as JSON object (if Content-Type is application/json)")
    private Object body;

    @Schema(description = "Base64-encoded response body for non-JSON content types")
    private String bodyText;

    @Schema(description = "Error message if request failed (empty on success)")
    private String error;

    @Schema(description = "Error category for classification",
            examples = {"validation", "authentication", "authorization", "not_found",
                    "timeout", "connection", "server_error", "unknown"})
    private String errorCategory;

    @Schema(description = "Whether the error is transient and the message can be retried")
    private boolean retryable;

    /**
     * Check if the response indicates a successful request.
     */
    public boolean isSuccess() {
        return (error == null || error.isEmpty()) && statusCode >= 200 && statusCode < 300;
    }

    /**
     * Check if the response has an error.
     */
    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

}

package com.czertainly.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Message envelope for proxy-to-core communication.
 * This is the top-level message received from the proxy via message queue.
 *
 * <p>Used for three message types:</p>
 * <ul>
 *   <li>Connector responses: Contains correlationId and connectorResponse</li>
 *   <li>Health checks: Contains only proxyId, messageType="health.check", and timestamp</li>
 *   <li>Connector registration: Contains connectorRegistrationRequest, messageType="connector.register"</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProxyMessage implements Serializable {

    @Schema(description = "Proxy instance identifier (subscription name)",
            examples = {"proxy-001", "proxy-azure-west"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String proxyId;

    @Schema(description = "Correlation ID echoed from request for matching. " +
            "Empty for fire-and-forget messages like health checks.",
            examples = {"550e8400-e29b-41d4-a716-446655440000"})
    private String correlationId;

    @Schema(description = "Message type for routing. Uses RabbitMQ topic exchange format with '.' as segment separator. " +
            "'health.check' for health check messages, or echoed from request for connector responses.",
            examples = {"health.check", "GET.v1.health", "POST.v1.authorityProvider.authorities"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageType;

    @Schema(description = "Timestamp of message creation (ISO8601)")
    private Instant timestamp;

    @Schema(description = "Connector response data. Null for health check messages.")
    private ConnectorResponse connectorResponse;

    @Schema(description = "Connector registration request data. Null for non-registration messages.")
    private ConnectorRegistrationRequest connectorRegistrationRequest;

    /**
     * Check if this is a health check message.
     */
    public boolean isHealthCheck() {
        return "health.check".equals(messageType);
    }

    /**
     * Check if this message has a connector response.
     */
    public boolean hasConnectorResponse() {
        return connectorResponse != null;
    }

    /**
     * Check if the connector response indicates success.
     * Returns false for health checks or messages without connector response.
     */
    public boolean isSuccess() {
        return connectorResponse != null && connectorResponse.isSuccess();
    }

    /**
     * Check if the connector response has an error.
     * Returns false for health checks or messages without connector response.
     */
    public boolean hasError() {
        return connectorResponse != null && connectorResponse.hasError();
    }

}

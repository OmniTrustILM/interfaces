package com.czertainly.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Message envelope for core-to-proxy communication.
 * This is the top-level message sent to the proxy via message queue.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoreMessage implements Serializable {

    @Schema(description = "Unique correlation ID for request/response matching",
            examples = {"550e8400-e29b-41d4-a716-446655440000"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String correlationId;

    @Schema(description = "Message type identifier for logging and routing. Uses RabbitMQ topic exchange format with '.' as segment separator.",
            examples = {"GET.v1.health", "POST.v1.authorityProvider.authorities"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageType;

    @Schema(description = "Timestamp of message creation (ISO8601)")
    private Instant timestamp;

    @Schema(description = "The actual HTTP request to be forwarded to the connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorRequest connectorRequest;

}

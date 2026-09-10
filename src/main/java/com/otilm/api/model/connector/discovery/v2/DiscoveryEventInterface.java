package com.otilm.api.model.connector.discovery.v2;

import com.otilm.api.model.connector.discovery.v2.event.DiscoveryErrorEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryHeartbeatEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryProgressEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryResultBatchEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryStateChangedEvent;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI schema for the polymorphic {@link DiscoveryEvent} hierarchy.
 *
 * <p>
 * {@code type} is the discriminator, declared on each subtype rather than on a wrapper. Do not add a
 * {@code {type, payload}} wrapper around it — that would only duplicate the discriminator and let the two copies
 * disagree.
 * </p>
 *
 * <p>
 * The Jackson subtype registry stays on {@link DiscoveryEvent}, for the reason spelled out on
 * {@link DiscoveredItemPayloadInterface}.
 * </p>
 */
@Schema(name = "DiscoveryEvent", description = "One flat discovery stream/AMQP event; type selects the concrete shape.",
        type = "object", discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = DiscoveryEventType.Codes.PROGRESS, schema = DiscoveryProgressEvent.class),
                @DiscriminatorMapping(value = DiscoveryEventType.Codes.RESULT_BATCH,
                        schema = DiscoveryResultBatchEvent.class),
                @DiscriminatorMapping(value = DiscoveryEventType.Codes.STATE_CHANGED,
                        schema = DiscoveryStateChangedEvent.class),
                @DiscriminatorMapping(value = DiscoveryEventType.Codes.HEARTBEAT,
                        schema = DiscoveryHeartbeatEvent.class),
                @DiscriminatorMapping(value = DiscoveryEventType.Codes.ERROR, schema = DiscoveryErrorEvent.class)},
        oneOf = {
                DiscoveryProgressEvent.class,
                DiscoveryResultBatchEvent.class,
                DiscoveryStateChangedEvent.class,
                DiscoveryHeartbeatEvent.class,
                DiscoveryErrorEvent.class})
public interface DiscoveryEventInterface {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    DiscoveryEventType getType();
}

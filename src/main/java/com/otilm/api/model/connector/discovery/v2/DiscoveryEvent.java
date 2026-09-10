package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryErrorEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryHeartbeatEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryProgressEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryResultBatchEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryStateChangedEvent;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * One event on a held-open NDJSON discovery stream — one flat JSON object per line — also used as the payload of the
 * AMQP {@code discovery.event} message, bound by Jackson on the {@code type} discriminator. The schema it publishes is
 * {@link DiscoveryEventInterface}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DiscoveryProgressEvent.class, name = DiscoveryEventType.Codes.PROGRESS),
        @JsonSubTypes.Type(value = DiscoveryResultBatchEvent.class, name = DiscoveryEventType.Codes.RESULT_BATCH),
        @JsonSubTypes.Type(value = DiscoveryStateChangedEvent.class, name = DiscoveryEventType.Codes.STATE_CHANGED),
        @JsonSubTypes.Type(value = DiscoveryHeartbeatEvent.class, name = DiscoveryEventType.Codes.HEARTBEAT),
        @JsonSubTypes.Type(value = DiscoveryErrorEvent.class, name = DiscoveryEventType.Codes.ERROR)})
@Schema(implementation = DiscoveryEventInterface.class)
public interface DiscoveryEvent extends DiscoveryEventInterface {
}

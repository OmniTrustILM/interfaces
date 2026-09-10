package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource-specific payload carried by a {@link DiscoveredItemDto}, bound by Jackson on the {@code resource}
 * discriminator. The schema it publishes is {@link DiscoveredItemPayloadInterface}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resource",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DiscoveredCertificateDto.class, name = Resource.Codes.CERTIFICATE),
        @JsonSubTypes.Type(value = DiscoveredKeyDto.class, name = Resource.Codes.CRYPTOGRAPHIC_KEY)})
@Schema(implementation = DiscoveredItemPayloadInterface.class)
public interface DiscoveredItemPayloadDto extends DiscoveredItemPayloadInterface {
}

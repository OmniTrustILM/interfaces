package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

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

    /**
     * The resources discovery can report: exactly those with a payload subtype registered above. Core and the connector
     * client read this set rather than keeping copies, so it must stay in step with the registration.
     */
    Set<Resource> DISCOVERABLE = Collections
            .unmodifiableSet(EnumSet.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));
}

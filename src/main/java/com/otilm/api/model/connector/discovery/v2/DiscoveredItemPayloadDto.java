package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Resource-specific payload carried by a {@link DiscoveredItemDto}. {@code resource} is this interface's own
 * discriminator, declared on each subtype ({@link DiscoveredCertificateDto}, {@link DiscoveredKeyDto}) rather than on
 * the enclosing item, since OpenAPI's discriminator requires the property to be present in every {@code oneOf}
 * subschema.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resource",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DiscoveredCertificateDto.class, name = Resource.Codes.CERTIFICATE),
        @JsonSubTypes.Type(value = DiscoveredKeyDto.class, name = Resource.Codes.CRYPTOGRAPHIC_KEY)})
@Schema(name = "DiscoveredItemPayload",
        description = "Resource-specific payload of a discovered item. The required resource property is "
                + "the discriminator: it carries the resource's wire code and selects which concrete "
                + "payload shape this object is. Each concrete shape fixes it to its own value.",
        type = "object", discriminatorProperty = "resource",
        discriminatorMapping = {
                @DiscriminatorMapping(value = Resource.Codes.CERTIFICATE, schema = DiscoveredCertificateDto.class),
                @DiscriminatorMapping(value = Resource.Codes.CRYPTOGRAPHIC_KEY, schema = DiscoveredKeyDto.class)},
        oneOf = {DiscoveredCertificateDto.class, DiscoveredKeyDto.class})
public interface DiscoveredItemPayloadDto {

    /**
     * The resources discovery can report — exactly those with a payload subtype registered above. A run may target a
     * resource precisely when this contract can carry an item for it, so Core and the connector client read this set
     * rather than keeping copies of it.
     */
    Set<Resource> DISCOVERABLE = Collections
            .unmodifiableSet(EnumSet.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));

    Resource getResource();
}

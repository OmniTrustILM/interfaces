package com.otilm.api.model.connector.discovery.v2;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI schema for the polymorphic {@link DiscoveredItemPayloadDto} hierarchy.
 *
 * <p>
 * {@code resource} is the discriminator, declared on each subtype ({@link DiscoveredCertificateDto},
 * {@link DiscoveredKeyDto}) rather than on the enclosing {@link DiscoveredItemDto}, since OpenAPI's discriminator
 * requires the property to be present in every {@code oneOf} subschema.
 * </p>
 *
 * <p>
 * The Jackson subtype registry stays on {@link DiscoveredItemPayloadDto}: {@code @JsonSubTypes} on a published union
 * makes swagger-core recompose each subtype as {@code allOf: [$ref union, own fields]}, a cycle no client generator can
 * express.
 * </p>
 */
@Schema(name = "DiscoveredItemPayload",
        description = "Resource-specific payload of a discovered item. The required resource property is "
                + "the discriminator: it carries the resource's wire code and selects which concrete "
                + "payload shape this object is. Each concrete shape fixes it to its own value.",
        type = "object", discriminatorProperty = "resource",
        discriminatorMapping = {
                @DiscriminatorMapping(value = Resource.Codes.CERTIFICATE, schema = DiscoveredCertificateDto.class),
                @DiscriminatorMapping(value = Resource.Codes.CRYPTOGRAPHIC_KEY, schema = DiscoveredKeyDto.class)},
        oneOf = {DiscoveredCertificateDto.class, DiscoveredKeyDto.class})
public interface DiscoveredItemPayloadInterface {

    // No description here on purpose: it would be hoisted onto the platform-wide Resource component
    // (the reasoning is spelled out on DiscoveredCertificateDto).
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Resource getResource();
}

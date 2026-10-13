package com.otilm.api.model.connector.discovery.v2;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI schema of the {@link DiscoveredItemPayloadDto} union. The {@code resource} discriminator is declared on each
 * subtype, since OpenAPI requires it in every {@code oneOf} member. The Jackson subtype registry stays on
 * {@link DiscoveredItemPayloadDto}: here it would make swagger-core emit an {@code allOf} cycle no client generator can
 * express.
 */
@Schema(name = "DiscoveredItemPayload",
        description = "Resource-specific payload of a discovered item. Its required resource property is the "
                + "discriminator: the resource code selects the payload shape.",
        type = "object", discriminatorProperty = "resource",
        discriminatorMapping = {
                @DiscriminatorMapping(value = Resource.Codes.CERTIFICATE, schema = DiscoveredCertificateDto.class),
                @DiscriminatorMapping(value = Resource.Codes.CRYPTOGRAPHIC_KEY, schema = DiscoveredKeyDto.class)},
        oneOf = {DiscoveredCertificateDto.class, DiscoveredKeyDto.class})
public interface DiscoveredItemPayloadInterface {

    // No description: it would be hoisted onto the shared Resource component.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Resource getResource();
}

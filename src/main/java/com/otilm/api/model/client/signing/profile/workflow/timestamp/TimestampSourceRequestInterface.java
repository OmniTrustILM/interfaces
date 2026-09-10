package com.otilm.api.model.client.signing.profile.workflow.timestamp;

import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadInterface;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * OpenAPI schema for the polymorphic {@link TimestampSourceRequestDto} hierarchy.
 *
 * <p>
 * The Jackson subtype registry stays on {@link TimestampSourceRequestDto}, for the reason spelled out on
 * {@link DiscoveredItemPayloadInterface}.
 * </p>
 */
@Schema(name = "TimestampSourceRequestDto",
        description = "Source of the timestamps a content-signing profile embeds, as supplied on create and update. "
                + "The required type property is the discriminator selecting the source.",
        type = "object", discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = TimestampSourceType.Codes.INTERNAL,
                        schema = InternalTimestampSourceRequestDto.class)},
        oneOf = {InternalTimestampSourceRequestDto.class})
public interface TimestampSourceRequestInterface extends Serializable {

    @Schema(description = "Kind of timestamp source, and the discriminator selecting the fields that accompany it",
            requiredMode = Schema.RequiredMode.REQUIRED, examples = {TimestampSourceType.Codes.INTERNAL})
    TimestampSourceType getType();
}

package com.otilm.api.model.client.signing.profile.workflow.timestamp;

import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadInterface;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * OpenAPI schema for the polymorphic {@link TimestampSourceDto} hierarchy.
 *
 * <p>
 * The Jackson subtype registry stays on {@link TimestampSourceDto}, for the reason spelled out on
 * {@link DiscoveredItemPayloadInterface}.
 * </p>
 */
@Schema(name = "TimestampSourceDto",
        description = "Source of the timestamps a content-signing profile embeds, as currently configured on the "
                + "profile. The required type property is the discriminator selecting the source.",
        type = "object", discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = TimestampSourceType.Codes.INTERNAL,
                        schema = InternalTimestampSourceDto.class)},
        oneOf = {InternalTimestampSourceDto.class})
public interface TimestampSourceInterface extends Serializable {

    @Schema(description = "Kind of timestamp source, and the discriminator selecting the fields that accompany it",
            requiredMode = Schema.RequiredMode.REQUIRED, examples = {TimestampSourceType.Codes.INTERNAL})
    TimestampSourceType getType();
}

package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One item a discovery run produced: a certificate or a cryptographic key. {@code resource} lives on {@code payload} as
 * its discriminator; {@link #getResource()} only unwraps it.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveredItemDto {

    @Schema(description = "Position of the item in the run, counting 1, 2, 3, ... with no gaps. Drain cursors count "
            + "in it.", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "sequence is required")
    @Min(value = 1, message = "sequence must be positive")
    private Long sequence;

    @Schema(description = "Stable identifier of the item in the run; Core deduplicates items by it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "uniqueRef is required")
    private String uniqueRef;

    @Schema(description = "Resource-specific payload, typed by its resource property",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "payload is required")
    @Valid
    private DiscoveredItemPayloadDto payload;

    // Not @Valid: MetadataAttribute declares no constraints to cascade into.
    @Schema(description = "Item metadata, such as where the item was found (IP and port, file path, HSM slot). "
            + "Location belongs here, never in payload.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "When the connector discovered the item", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime discoveredAt;

    /** The payload's resource, or null without a payload. Not serialized. */
    @JsonIgnore
    public Resource getResource() {
        return payload != null ? payload.getResource() : null;
    }
}

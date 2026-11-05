package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response of the discovery v2 results call: a page of discovered items.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveryResultsResponseDto {

    @Schema(description = "Items after the requested cursor, within the batch limits. A page with no items MUST be "
            + "an empty array; an omitted field is rejected.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "items is required (send an explicit empty array for a page with no items)")
    private List<@NotNull(message = "items must not contain a null item") @Valid DiscoveredItemDto> items;

    @Schema(description = "Highest item sequence assigned in the whole run so far, not just this page; 0 means none "
            + "yet. Advance a cursor only by sequences actually received.", requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0")
    @NotNull(message = "highestSequence is required")
    @PositiveOrZero(message = "highestSequence must not be negative")
    private Long highestSequence;

    @Schema(description = "true means more is ready and Core calls again now; false means nothing more is ready now. "
            + "Whether the run has finished comes from status.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "more is required")
    private Boolean more;

    // Excluded from toString: opaque, no logging value, up to 64 KB.
    @Schema(description = "Checkpointed delivery only: the run's handle after this page's items, present on every "
            + "results response. Core stores it with its cursor and replays it on the next call. At most 64 KB "
            + "serialized. Absent in buffered delivery.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;
}

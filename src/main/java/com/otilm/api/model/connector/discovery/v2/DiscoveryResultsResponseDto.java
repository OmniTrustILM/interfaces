package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @Schema(description = "Whether more items remain beyond this page", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "more is required")
    private Boolean more;
}

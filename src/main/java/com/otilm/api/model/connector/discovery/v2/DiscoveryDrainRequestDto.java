package com.otilm.api.model.connector.discovery.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for the discovery v2 /results call: pulls a batch of discovered items produced after the given sequence cursor.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DiscoveryDrainRequestDto extends DiscoveryV2ScopedRequestDto {

    /**
     * Hard cap on {@code maxBytes}: 10 MiB, the largest single response the platform's tunneled transport carries.
     * Declared once and referenced by {@code maxBytes}'s bean-validation bound, its published schema {@code maximum},
     * and its prose, so the three cannot disagree.
     */
    public static final long MAX_BYTES_CAP = 10_485_760L;

    @Schema(description = "Cursor: return items with sequence greater than this value. "
            + "Item sequences are dense per run, so 0 requests items from the start of the run.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0", defaultValue = "0")
    @PositiveOrZero
    private long afterSequence;

    @Schema(description = "Maximum number of items to return in this batch. The platform's tunneled "
            + "transport caps a single response at 10 MiB; a connector MUST apply its own bound on "
            + "the returned page when this is omitted.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    @Min(1)
    private Integer maxItems;

    @Schema(description = "Maximum serialized size, in bytes, of the items returned in this batch. The "
            + "platform's tunneled transport caps a single response at 10 MiB (" + MAX_BYTES_CAP
            + " bytes); exceeding it is an unrecoverable run failure. A connector MUST apply its own "
            + "bound when this is omitted.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1",
            maximum = "" + MAX_BYTES_CAP)
    @Min(1)
    @Max(value = MAX_BYTES_CAP, message = "maxBytes must not exceed the 10 MiB transport cap")
    private Long maxBytes;
}

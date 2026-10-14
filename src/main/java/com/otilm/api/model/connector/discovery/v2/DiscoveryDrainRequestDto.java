package com.otilm.api.model.connector.discovery.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body of the discovery v2 results call: a page of items after the given sequence.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DiscoveryDrainRequestDto extends DiscoveryV2ScopedRequestDto {

    /**
     * Cap on {@code maxBytes}: 10 MiB, the largest response the platform's tunneled transport carries. Shared by the
     * validation bound, the schema maximum and the description.
     */
    public static final long MAX_BYTES_CAP = 10_485_760L;

    @Schema(description = "Return items with a sequence greater than this; 0 starts from the beginning of the run.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0", defaultValue = "0")
    @PositiveOrZero
    private long afterSequence;

    @Schema(description = "Maximum number of items to return. When omitted, the connector MUST apply its own bound.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    @Min(1)
    private Integer maxItems;

    @Schema(description = "Maximum serialized size, in bytes, of the returned items: at most 10 MiB (" + MAX_BYTES_CAP
            + " bytes), the transport's limit, and a larger response fails the run. When omitted, the connector MUST "
            + "apply its own bound.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1",
            maximum = "" + MAX_BYTES_CAP)
    @Min(1)
    @Max(value = MAX_BYTES_CAP, message = "maxBytes must not exceed the 10 MiB transport cap")
    private Long maxBytes;
}

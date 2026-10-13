package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What a run has found for one resource type. Items only; the outcomes of the work are in {@link DiscoveryProgressDto}.
 */
@Getter
@Setter
@ToString
@Schema(description = "Items found for one resource type, keyed by resource code in byResource. Every field is "
        + "optional, but a provider with nothing to report MUST leave the entry out rather than send an empty object.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryResourceProgressDto {

    @Schema(description = "Items found so far. Omitted if the provider cannot count them.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long produced;

    @Schema(description = "Estimated total for the whole discovery. Omitted if the provider cannot estimate it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long totalEstimate;

    @Schema(description = "What the provider is doing with this resource type, as free text. Omitted if it has "
            + "none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;
}

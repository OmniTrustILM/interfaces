package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What a run has yielded for one resource type — the leaf of the progress model. Item counts only: a failure belongs to
 * the work a connector attempted, not to any one resource, so {@link DiscoveryProgressDto} counts that.
 */
@Getter
@Setter
@ToString
@Schema(description = "How many items of one resource type have been found, keyed by resource code inside "
        + "byResource (e.g. \"certificates\", \"keys\"). Every field is optional, but a provider with nothing to "
        + "report MUST leave the whole object out rather than send an empty one — a client keeping the last "
        + "progress it was sent cannot tell an empty report from a missing one.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryResourceProgressDto {

    @Schema(description = "How many items of this resource type the provider has found so far; omitted if it "
            + "cannot count them", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long produced;

    @Schema(description = "Estimated total number of items of this resource type for the whole discovery; "
            + "omitted if the provider cannot estimate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long totalEstimate;

    @Schema(description = "Free-text label for what the provider is doing with this resource type; omitted if "
            + "it has none", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;
}

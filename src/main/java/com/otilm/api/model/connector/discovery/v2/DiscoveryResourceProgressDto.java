package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What a run has yielded for one resource type. The leaf of the progress model: it carries item counts only, since a
 * failure belongs to the work a connector attempted rather than to any one resource — {@link DiscoveryProgressDto}
 * counts that.
 */
@Getter
@Setter
@ToString
@Schema(description = "Item counts for one resource type, keyed by resource code inside byResource (e.g. "
        + "\"certificates\", \"keys\"). Every field is optional, and a producer with nothing to report MUST "
        + "omit the whole object rather than send one whose fields are all absent: a consumer keeping the last "
        + "progress it was told cannot tell an empty report from a missing one.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryResourceProgressDto {

    @Schema(description = "Number of items of this resource produced so far; omitted when the connector cannot "
            + "report it", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long processed;

    @Schema(description = "Estimated total number of items of this resource for the run; omitted when the "
            + "connector cannot produce an estimate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long totalEstimate;

    @Schema(description = "Connector-defined free-text phase label for this resource; omitted when the connector "
            + "has no per-resource phase concept", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;
}

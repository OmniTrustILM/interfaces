package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response of the discovery v2 initiate and resume calls.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryInitiateResponseDto {

    // Excluded from toString: opaque, no logging value, up to 64 KB.
    @Schema(description = "Opaque run handle. Core replays it on status, results, stop, resume and cancel and never "
            + "shows it. At most 64 KB serialized; a larger one fails the run.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;

    @Schema(description = "Whether this run can be stopped and resumed. Can only narrow the discoveryStopResume "
            + "feature flag: true without the flag still means not stoppable, and absent leaves the flag to decide. A "
            + "resume response replaces the value. A stop can still be refused past the point of no return.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean stoppable;
}

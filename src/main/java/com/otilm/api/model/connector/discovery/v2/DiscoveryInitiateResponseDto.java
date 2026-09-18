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
 * Body returned by the discovery v2 /initiate call.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryInitiateResponseDto {

    // Excluded from toString: opaque, no logging value, up to 64 KB.
    @Schema(description = "Opaque run handle the stateless connector resolves its run state from. Core replays it "
            + "on status, results, stream, stop, resume and cancel, and never renders it. Serialized size is capped "
            + "at 64 KB; over the cap Core fails the run.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;

    @Schema(description = "Whether this run can be stopped and later resumed. May only narrow the "
            + "discoveryStopResume feature flag, never widen it: true without the flag is clamped to not-stoppable. "
            + "Absent: Core gates on the flag alone. Each resume response replaces the value; omitting it there "
            + "reverts the run to flag-gating. The connector may still refuse a stop past the point of no return.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean stoppable;
}

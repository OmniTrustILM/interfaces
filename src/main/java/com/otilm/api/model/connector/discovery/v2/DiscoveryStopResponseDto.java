package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body returned by the discovery v2 /stop call.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveryStopResponseDto {

    // Excluded from toString: opaque, no logging value, up to 64 KB.
    @Schema(description = "Checkpoint to resume from: the opaque run handle as of the stop, replayed by Core on "
            + "status, results, stream, stop, resume and cancel, and never rendered. Serialized size is capped at "
            + "64 KB; over the cap Core fails the run.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;
}

package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response of the discovery v2 stop call.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveryStopResponseDto {

    // Excluded from toString: opaque, no logging value, up to 64 KB.
    @Schema(description = "Opaque run handle to resume from. Core replays it on status, results, stop, resume and "
            + "cancel and never shows it. At most 64 KB serialized; a larger one fails the run.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;
}

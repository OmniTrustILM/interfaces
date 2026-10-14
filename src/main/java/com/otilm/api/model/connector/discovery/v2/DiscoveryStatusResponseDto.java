package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response of the discovery v2 status call.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveryStatusResponseDto {

    @Schema(description = "Current state of the run", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "state is required")
    private DiscoveryRunState state;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "How far the run has got. Omitted when the connector cannot report progress.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    private DiscoveryProgressDto progress;

    @Schema(description = "Highest item sequence assigned in the run so far; 0 means none yet. Advance a cursor only "
            + "by sequences actually received.", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "highestSequence is required")
    @PositiveOrZero(message = "highestSequence must not be negative")
    private Long highestSequence;

    // NON_NULL rather than NON_EMPTY on purpose: an empty list is a statement and must reach the wire.
    @Schema(description = "Metadata about the run as a whole, for a person to read, such as the scan window or why "
            + "targets failed. Absent keeps what Core holds, present replaces it, and an empty list clears it. Over "
            + "64 KB serialized it is dropped with a run message, and the run goes on. Counts that progress has a "
            + "field for belong there.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}

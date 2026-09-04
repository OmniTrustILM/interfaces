package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body returned by the discovery v2 /status call.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveryStatusResponseDto {

    @Schema(description = "Current state of the discovery run", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "state is required")
    private DiscoveryRunState state;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "How far the run has got. Omitted when the connector cannot report progress.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    private DiscoveryProgressDto progress;

    @Schema(description = "Run-wide highest item sequence assigned so far — never page-scoped. Consumers must "
            + "advance cursors only by item sequences actually received. Item sequences start at 1, so 0 "
            + "means the run has produced no items yet.", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "highestSequence is required")
    @PositiveOrZero(message = "highestSequence must not be negative")
    private Long highestSequence;
}

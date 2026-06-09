package com.otilm.api.model.messaging.timequality;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "TimeQualityConfigSnapshot", description = "Full snapshot of all NTP-based time quality configurations, sent by Core to Time Quality Monitor")
public class TimeQualityConfigSnapshot implements Serializable {

    @Schema(description = "Correlation identifier carried from the matching TimeQualityConfigRequest. Null when Core publishes the snapshot proactively.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID correlationId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timestamp when the snapshot was generated, in ISO 8601 UTC format", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant generatedAt;

    @NotNull
    @Valid
    @Schema(description = "Complete list of active NTP-based time quality configurations; always a full snapshot, never a delta", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TimeQualityConfig> configurations = new ArrayList<>();
}

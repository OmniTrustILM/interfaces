package com.czertainly.api.model.messaging.timequality;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Schema(name = "TimeQualityConfigSnapshotMessage", description = "Full snapshot of all NTP-based time quality configurations, sent by Core to Time Quality Monitor")
public class TimeQualityConfigSnapshotMessage {

    @Schema(description = "Timestamp when the snapshot was generated, in ISO 8601 UTC format", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant generatedAt;

    @Schema(description = "Complete list of active NTP-based time quality configuration; always a full snapshot, never a delta", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TimeQualityConfigurationMessage> configurations;
}

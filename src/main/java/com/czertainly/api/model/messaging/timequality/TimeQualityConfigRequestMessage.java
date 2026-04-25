package com.czertainly.api.model.messaging.timequality;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(name = "TimeQualityConfigRequestMessage", description = "Sent by Time Quality Monitor to Core to request the current time quality configuration snapshot")
public class TimeQualityConfigRequestMessage {

    @Schema(description = "Timestamp when the config request was sent, in ISO 8601 UTC format", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant requestedAt;
}

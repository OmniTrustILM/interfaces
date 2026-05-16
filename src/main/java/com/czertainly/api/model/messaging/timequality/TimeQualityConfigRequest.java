package com.czertainly.api.model.messaging.timequality;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Schema(name = "TimeQualityConfigRequest", description = "Sent by Time Quality Monitor to Core to request the current time quality configuration snapshot")
public class TimeQualityConfigRequest implements Serializable {

    @NotNull
    @Schema(description = "Unique correlation identifier carried back in the matching TimeQualityConfigSnapshot so the requester can pair the response with this request",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID correlationId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timestamp when the config request was sent, in ISO 8601 UTC format", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant requestedAt;
}

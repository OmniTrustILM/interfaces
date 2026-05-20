package com.czertainly.api.model.messaging.timequality;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "TimeQualityResultMessage", description = "NTP time quality check result, published by Time Quality Monitor and consumed by Core")
public class TimeQualityResultMessage implements Serializable {

    @NotNull
    @Schema(description = "UUID of the time quality configuration that produced this result", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID configurationId;

    @NotBlank
    @Schema(description = "Display name of the time quality configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timestamp when the check was performed, in ISO 8601 UTC format", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant timestamp;

    @NotNull
    @Schema(description = "Overall time quality status. DEGRADED is always set when NTP leap indicators conflict across servers (LeapUnsync), regardless of the profile's leapSecondGuard setting", requiredMode = Schema.RequiredMode.REQUIRED)
    private TimeQualityStatus status;

    @Schema(description = "Measured clock drift from NTP reference time in milliseconds; null when drift could not be determined", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Double measuredDriftMs;

    @PositiveOrZero
    @Schema(description = "Number of NTP servers that were reachable during this check", requiredMode = Schema.RequiredMode.REQUIRED)
    private int reachableServers;

    @Schema(description = "Human-readable reason for the resulting status, if applicable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @NotNull
    @Schema(description = "Leap second warning derived from NTP leap indicators. Set to NONE when indicators conflict", requiredMode = Schema.RequiredMode.REQUIRED)
    private LeapSecondWarning leapSecondWarning;

    @NotNull
    @Valid
    @Schema(description = "Per-server measurement details for this check", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<NtpServerMeasurementResult> measurements;
}

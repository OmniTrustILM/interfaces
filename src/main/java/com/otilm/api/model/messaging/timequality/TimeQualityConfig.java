package com.otilm.api.model.messaging.timequality;

import com.otilm.api.model.client.signing.timequality.validation.NtpConfiguration;
import com.otilm.api.model.client.signing.timequality.validation.ValidNtpMinReachable;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Data
@ValidNtpMinReachable
@Schema(name = "TimeQualityConfig", description = "NTP-based time quality configuration carried within a configuration snapshot")
// Compared to TimeQualityConfigurationDto, accuracy is intentionally missing. It is only relevant for `TSTInfo` construction and has no bearing on time quality monitoring.
public class TimeQualityConfig implements Serializable, NtpConfiguration {

    @NotNull
    @Schema(description = "Unique identifier of the time quality configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @NotBlank
    @Schema(description = "Display name of the time quality configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotEmpty
    @Schema(description = "List of NTP server addresses used for time quality checks", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<@NotBlank String> ntpServers;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Interval between NTP checks, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT0.5S")
    private Duration ntpCheckInterval;

    @Positive
    @Schema(description = "Number of NTP samples to take per server during each check", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private int ntpSamplesPerServer;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timeout for the entire NTP check cycle, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT0.1S")
    private Duration ntpCheckTimeout;

    @Positive
    @Schema(description = "Minimum number of NTP servers that must be reachable for the check to be valid", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int ntpServersMinReachable;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Maximum allowed clock drift from the NTP reference time, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT1S")
    private Duration maxClockDrift;

    @Schema(description = "Whether to guard against leap second anomalies. When true, signing operations are blocked for approximately two seconds around midnight "
    + "on days when NTP servers announce an upcoming leap second. Set to false to allow signing through that window at the cost of potential one-second timestamp "
    + "inaccuracy during the leap event.", example = "true")
    private boolean leapSecondGuard;
}

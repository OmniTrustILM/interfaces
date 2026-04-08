package com.czertainly.api.model.client.signing.timequality;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.signing.timequality.validation.PositiveDuration;
import com.czertainly.api.model.client.signing.timequality.validation.ValidHostnameList;
import com.czertainly.api.model.common.validation.ValidName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "TimeQualityConfigurationCreateRequestDto", description = "Request to create a new Time Quality Configuration")
public class TimeQualityConfigurationCreateRequestDto {

    @NotBlank
    @ValidName
    @Schema(description = "Name of the Time Quality Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "NTP-Config-1")
    private String name;

    @NotNull
    @PositiveDuration
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Desired accuracy for the time quality, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT1S", defaultValue = "PT1S")
    private Duration accuracy;

    @NotNull
    @NotEmpty
    @ValidHostnameList
    @Schema(description = "List of NTP server addresses", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<String> ntpServers;

    @PositiveDuration
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Interval between NTP checks, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT30S", defaultValue = "PT30S")
    private Duration ntpCheckInterval = Duration.ofSeconds(30);

    @Positive
    @Schema(description = "Number of NTP samples to take per server during each check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "4", defaultValue = "4")
    private int ntpSamplesPerServer = 4;

    @PositiveDuration
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timeout for a single NTP check, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT5S", defaultValue = "PT5S")
    private Duration ntpCheckTimeout = Duration.ofSeconds(5);

    @Positive
    @Schema(description = "Minimum number of NTP servers that must be reachable", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1", defaultValue = "1")
    private int ntpServersMinReachable = 1;

    @PositiveDuration
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Maximum allowed clock drift from NTP reference time, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT1S", defaultValue = "PT1S")
    private Duration maxClockDrift = Duration.ofSeconds(1);

    @Schema(description = "Whether to guard against leap second anomalies", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true", defaultValue = "true")
    private boolean leapSecondGuard = true;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}

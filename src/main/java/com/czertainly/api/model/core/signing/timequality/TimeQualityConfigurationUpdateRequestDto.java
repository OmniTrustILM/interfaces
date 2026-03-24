package com.czertainly.api.model.core.signing.timequality;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import com.czertainly.api.model.core.signing.timequality.validation.PositiveDuration;
import com.czertainly.api.model.core.signing.timequality.validation.ValidHostnameList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "TimeQualityConfigurationUpdateRequestDto", description = "Request to update an existing Time Quality Configuration")
public class TimeQualityConfigurationUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the Time Quality Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "NTP-Config-1")
    private String name;

    @NotNull
    @NotEmpty
    @ValidHostnameList
    @Schema(description = "List of NTP server addresses", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<String> ntpServers;

    @PositiveDuration
    @Schema(description = "Interval between NTP checks, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT30S", defaultValue = "PT30S")
    private Duration ntpCheckInterval = Duration.ofSeconds(30);

    @Positive
    @Schema(description = "Number of NTP samples to take per server during each check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "4", defaultValue = "4")
    private int ntpSamplesPerServer = 4;

    @PositiveDuration
    @Schema(description = "Timeout for a single NTP check, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT5S", defaultValue = "PT5S")
    private Duration ntpCheckTimeout = Duration.ofSeconds(5);

    @Positive
    @Schema(description = "Minimum number of NTP servers that must be reachable", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1", defaultValue = "1")
    private int minReachable = 1;

    @PositiveDuration
    @Schema(description = "Maximum allowed clock drift from NTP reference time, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT1S", defaultValue = "PT1S")
    private Duration maxDrift = Duration.ofSeconds(1);

    @Schema(description = "Whether to guard against leap second anomalies", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true", defaultValue = "true")
    private boolean leapSecondGuard = true;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}

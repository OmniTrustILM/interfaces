package com.czertainly.api.model.client.signing.timequality;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TimeQualityConfigurationDto", description = "Time quality configuration details")
@ToString(callSuper = true)
public class TimeQualityConfigurationDto extends NameAndUuidDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Declared accuracy of the profile, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT1S", defaultValue = "PT1S")
    private Duration accuracy = Duration.ofSeconds(1);

    @Schema(description = "List of NTP server addresses", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<String> ntpServers;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Interval between NTP checks, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT30S", defaultValue = "PT30S")
    private Duration ntpCheckInterval = Duration.ofSeconds(30);

    @Schema(description = "Number of NTP samples to take per server during each check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "3", defaultValue = "3")
    private int ntpSamplesPerServer = 3;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timeout for the entire NTP check cycle, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT10S", defaultValue = "PT10S")
    private Duration ntpCheckTimeout = Duration.ofSeconds(10);

    @Schema(description = "Minimum number of NTP servers that must be reachable", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1", defaultValue = "1")
    private int ntpServersMinReachable = 1;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Maximum allowed clock drift from NTP reference time, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT500MS", defaultValue = "PT500MS")
    private Duration maxClockDrift = Duration.ofMillis(500);

    @Schema(description = "Whether to guard against leap second anomalies", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true", defaultValue = "true")
    private boolean leapSecondGuard = true;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}

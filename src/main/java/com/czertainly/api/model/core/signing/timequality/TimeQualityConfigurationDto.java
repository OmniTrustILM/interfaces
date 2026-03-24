package com.czertainly.api.model.core.signing.timequality;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
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

    @Schema(description = "List of NTP server addresses", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<String> ntpServers;

    @Schema(description = "Interval between NTP checks, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT30S", defaultValue = "PT30S")
    private Duration ntpCheckInterval = Duration.ofSeconds(30);

    @Schema(description = "Number of NTP samples to take per server during each check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "4", defaultValue = "4")
    private int ntpSamplesPerServer = 4;

    @Schema(description = "Timeout for a single NTP check, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT5S", defaultValue = "PT5S")
    private Duration ntpCheckTimeout = Duration.ofSeconds(5);

    @Schema(description = "Minimum number of NTP servers that must be reachable", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1", defaultValue = "1")
    private int minReachable = 1;

    @Schema(description = "Maximum allowed clock drift from NTP reference time, in ISO 8601 duration format", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PT1S", defaultValue = "PT1S")
    private Duration maxDrift = Duration.ofSeconds(1);

    @Schema(description = "Whether to guard against leap second anomalies", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true", defaultValue = "true")
    private boolean leapSecondGuard = true;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}

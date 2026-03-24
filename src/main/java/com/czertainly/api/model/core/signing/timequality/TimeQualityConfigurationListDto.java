package com.czertainly.api.model.core.signing.timequality;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TimeQualityConfigurationListDto", description = "Time quality configuration details for listing")
@ToString(callSuper = true)
public class TimeQualityConfigurationListDto extends NameAndUuidDto {

    @Schema(description = "List of NTP server addresses", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"pool.ntp.org\", \"time.google.com\"]")
    private List<String> ntpServers;
}

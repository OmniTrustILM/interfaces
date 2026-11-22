package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.discovery.validation.ConsistentLookback;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConsistentLookback
public class DiscoveryWindowDto {

    @Schema(description = "How much of the target the discovery covers. sinceLastSuccessfulRun is accepted only when "
            + "scheduling a discovery.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "mode is required")
    private DiscoveryWindowMode mode;

    @Schema(description = "How far back from the start of the run to discover, as an ISO-8601 duration. Required, and "
            + "positive, for lookback; not allowed for the other modes.", type = "string", format = "duration",
            example = "P1D", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration lookback;
}

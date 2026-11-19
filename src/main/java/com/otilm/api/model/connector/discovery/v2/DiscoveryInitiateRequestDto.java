package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body of the discovery v2 initiate call.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DiscoveryInitiateRequestDto extends DiscoveryV2ScopedRequestDto {

    @Schema(description = "Discover only items created or changed at or after this instant, as far as the technology "
            + "can tell; absent means a full discovery. Sent only to a connector whose discovery interface advertises "
            + "discoveryIncremental.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime discoverSince;
}

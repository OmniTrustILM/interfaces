package com.otilm.api.model.common.events.data;

import com.otilm.api.model.core.discovery.DiscoveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class DiscoveryFinishedEventData implements EventData {
    @Schema(description = "Discovery UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID discoveryUuid;

    @Schema(description = "Discovery name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String discoveryName;

    @Schema(description = "UUID of user that run discovery", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID discoveryUserUuid;

    @Schema(description = "Discovery connector UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID discoveryConnectorUuid;

    @Schema(description = "Discovery connector name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String discoveryConnectorName;

    @Schema(description = "Discovery status", requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryStatus discoveryStatus;

    @Schema(description = "Total certificates discovered", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalCertificateDiscovered;

    @Schema(description = "Discovery message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String discoveryMessage;

}

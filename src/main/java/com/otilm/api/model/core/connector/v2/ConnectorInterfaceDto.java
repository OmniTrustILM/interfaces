package com.otilm.api.model.core.connector.v2;

import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.client.connector.v2.FeatureFlag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class ConnectorInterfaceDto implements Serializable {

    @Schema(description = "UUID of the connector interface", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Code of the implemented connector interface", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorInterface code;

    @Schema(description = "Version of the implemented connector interface", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @Schema(description = "Features supported by the connector interface", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<FeatureFlag> features;

}

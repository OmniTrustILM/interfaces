package com.otilm.api.model.client.connector.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorInterfaceInfo implements Serializable {

    @Schema(description = "Code of the implemented connector interface", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorInterface code;

    @Schema(description = "Version of the implemented connector interface", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @Schema(description = "Feature flags the connector advertises for the interface: capabilities it supports "
            + "and restrictions it declares", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<FeatureFlag> features;

}

package com.otilm.api.model.client.connector.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class InfoResponse {

    @Schema(description = "Connector Information", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorInfo connector;

    @Schema(description = "Interfaces supported and implemented by the connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ConnectorInterfaceInfo> interfaces;
}

package com.otilm.api.model.client.connector.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class ConnectorInfo {

    @Schema(description = "Unique identifier of the connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "Name of the connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Version of the connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @Schema(description = "Description of the connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Metadata of the connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> metadata;

}

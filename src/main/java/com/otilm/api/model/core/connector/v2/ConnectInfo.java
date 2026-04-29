package com.otilm.api.model.core.connector.v2;

import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "version", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectInfoV1.class, name = ConnectorVersion.Codes.V1),
        @JsonSubTypes.Type(value = ConnectInfoV2.class, name = ConnectorVersion.Codes.V2)
})
@Schema(implementation = ConnectInfoDto.class)
public abstract class ConnectInfo {

    @Schema(description = "Version of the Connector.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = ConnectorVersion.Codes.V2)
    private ConnectorVersion version;

    @Schema(description = "Connector UUID. Present when connector with that version is already added.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID connectorUuid;

    @Schema(description = "Error message in case of connection failure.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String errorMessage;

    public static ConnectInfo fromError(ConnectorVersion version, String errorMessage) {
        return version == ConnectorVersion.V1 ? new ConnectInfoV1(errorMessage) : new ConnectInfoV2(errorMessage);
    }

}

package com.otilm.api.model.core.connector.v2;

import com.otilm.api.model.client.connector.v2.ConnectorInfo;
import com.otilm.api.model.client.connector.v2.ConnectorInterfaceInfo;
import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectInfoV2 extends ConnectInfo {

    @Schema(description = "Connector Information", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConnectorInfo connector;

    @Schema(description = "Interfaces supported and implemented by the connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ConnectorInterfaceInfo> interfaces = new ArrayList<>();

    public ConnectInfoV2() {
        this.setVersion(ConnectorVersion.V2);
    }

    public ConnectInfoV2(String errorMessage) {
        this.setVersion(ConnectorVersion.V2);
        this.setErrorMessage(errorMessage);
    }

}

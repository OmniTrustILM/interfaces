package com.otilm.api.model.core.connector.v2;

import com.otilm.api.model.client.connector.v2.ConnectorInfo;
import com.otilm.api.model.client.connector.v2.ConnectorInterfaceInfo;
import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.ContentSigningDeclarationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectInfoV2 extends ConnectInfo {

    @Schema(description = "Connector Information", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConnectorInfo connector;

    @Schema(description = "Interfaces supported and implemented by the connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ConnectorInterfaceInfo> interfaces = new ArrayList<>();

    @Schema(description = "Content-signing families the connector declares; present when it implements the "
            + "contentSigningFormatting interface", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ContentSigningDeclarationDto contentSigningDeclaration;

    public ConnectInfoV2() {
        this.setVersion(ConnectorVersion.V2);
    }

    public ConnectInfoV2(String errorMessage) {
        this.setVersion(ConnectorVersion.V2);
        this.setErrorMessage(errorMessage);
    }

}

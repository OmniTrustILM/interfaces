package com.otilm.api.model.core.connector.v2;

import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import com.otilm.api.model.core.connector.FunctionGroupDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectInfoV1 extends ConnectInfo {

    @Schema(description = "Function Group information of a connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FunctionGroupDto> functionGroups = new ArrayList<>();

    public ConnectInfoV1() {
        this.setVersion(ConnectorVersion.V1);
    }

    public ConnectInfoV1(String errorMessage) {
        this.setVersion(ConnectorVersion.V1);
        this.setErrorMessage(errorMessage);
    }
}

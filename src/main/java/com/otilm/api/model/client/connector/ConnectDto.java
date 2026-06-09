package com.otilm.api.model.client.connector;

import com.otilm.api.model.core.connector.FunctionGroupDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ConnectDto {

    @Schema(description = "Function Group information of a connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private FunctionGroupDto functionGroup;
}

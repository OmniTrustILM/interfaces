package com.otilm.api.model.core.connector;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BaseFunctionGroupDto implements Serializable {

    @Schema(description = "Enumerated code of functional group",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected FunctionGroupCode functionGroupCode;

    @Schema(description = "List of supported functional group kinds",
            example = "[\"SoftKeyStore\", \"Basic\", \"ApiKey\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected List<String> kinds;

    @Schema(description = "List of end points related to functional group",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected List<EndpointDto> endPoints;

    public BaseFunctionGroupDto() {
        super();
    }

    public BaseFunctionGroupDto(List<String> kinds, FunctionGroupCode functionGroupCode, List<EndpointDto> endPoints) {
        super();
        this.kinds = kinds;
        this.functionGroupCode = functionGroupCode;
        this.endPoints = endPoints;
    }
}

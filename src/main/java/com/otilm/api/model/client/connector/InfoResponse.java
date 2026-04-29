package com.otilm.api.model.client.connector;

import com.otilm.api.model.core.connector.BaseFunctionGroupDto;
import com.otilm.api.model.core.connector.EndpointDto;
import com.otilm.api.model.core.connector.FunctionGroupCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class InfoResponse extends BaseFunctionGroupDto {

    public InfoResponse() {
        super();
    }

    public InfoResponse(List<String> kinds, FunctionGroupCode functionGroupCode, List<EndpointDto> endPoints) {
        super(kinds, functionGroupCode, endPoints);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("functionGroupCode", functionGroupCode)
                .append("kinds", kinds)
                .append("endPoints", endPoints)
                .toString();
    }
}

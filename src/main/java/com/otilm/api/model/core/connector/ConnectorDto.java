package com.otilm.api.model.core.connector;

import io.swagger.v3.oas.annotations.media.Schema;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ConnectorDto extends ConnectorApiClientDto {

    @Schema(description = "List of Function Groups implemented by the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FunctionGroupDto> functionGroups;
    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("functionGroups", functionGroups)
                .append("url", getUrl())
                .append("authType", getAuthType())
                .append("authAttributes", getAuthAttributes())
                .append("status", getStatus())
                .append("proxy", getProxy())
                .append("name", name)
                .append("uuid", uuid)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

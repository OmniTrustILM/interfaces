package com.otilm.api.model.core.connector;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
@Schema(name = "ConnectorDto", description = "Connector details (V1)")
public class ConnectorDto extends ConnectorApiClientDtoV1 implements ObjectActionsAware {

    @Schema(description = "List of Function Groups implemented by the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FunctionGroupDto> functionGroups;
    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

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
                .append("allowedActions", allowedActions)
                .toString();
    }
}

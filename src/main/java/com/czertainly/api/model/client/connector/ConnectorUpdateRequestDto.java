package com.czertainly.api.model.client.connector;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.core.connector.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ConnectorUpdateRequestDto {

    @Schema(description = "URL of the Connector to connect",
            examples = {"http://network-discovery-provicer:8080"})
    private String url;
    @Schema(description = "Type of authentication for the Connector",
            examples = {"none"})
    private AuthType authType;
    @Schema(description = "List of authentication Attributes. Required if the authentication type is not NONE")
    private List<RequestAttribute> authAttributes;
    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "UUID of the Proxy",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String proxyUuid;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("url", url)
                .append("authType", authType)
                .append("authAttributes", authAttributes)
                .append("customAttributes", customAttributes)
                .append("proxyUuid", proxyUuid)
                .toString();
    }
}

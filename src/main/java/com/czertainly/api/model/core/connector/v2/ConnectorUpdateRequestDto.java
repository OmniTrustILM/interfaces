package com.czertainly.api.model.core.connector.v2;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.core.connector.AuthType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Schema(name = "ConnectorUpdateRequestDtoV2")
public class ConnectorUpdateRequestDto {

    @NotNull
    @URL
    @Schema(description = "URL of the Connector to connect",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"http://network-discovery-provider:8080"})
    private String url;

    @NotNull
    @Schema(description = "Type of authentication for the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"none"})
    private AuthType authType;

    @Schema(description = "List of authentication Attributes. Required if the authentication type is not NONE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> authAttributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();

    @Schema(description = "UUID of the Proxy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String proxyUuid;

    @AssertTrue(message = "Authentication Attributes must be provided when Authentication Type is not NONE")
    @JsonIgnore
    public boolean isValid() {
        return authType == AuthType.NONE || (authAttributes != null && !authAttributes.isEmpty());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("url", url).append("authType", authType).append("authAttributes", authAttributes).append("customAttributes", customAttributes).append("proxyUuid", proxyUuid).toString();
    }
}

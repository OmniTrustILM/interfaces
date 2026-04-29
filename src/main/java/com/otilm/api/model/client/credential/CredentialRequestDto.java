package com.otilm.api.model.client.credential;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class CredentialRequestDto implements Serializable {

    @Schema(description = "Credential name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Credential Kind",
            examples = {"SoftKeyStore, Basic, ApiKey, etc"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "List of Credential Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "UUID of Credential provider Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("kind", kind)
                .append("attributes", attributes)
                .append("connectorUuid", connectorUuid)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

package com.otilm.api.model.core.credential;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CredentialDto extends NameAndUuidDto implements Serializable {

    @Schema(description = "Credential Kind",
            examples = {"SoftKeyStore, Basic, ApiKey, etc"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "List of Credential Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes = new ArrayList<>();

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "UUID of Credential provider Connector",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorUuid;

    @Schema(description = "Name of Credential provider Connector",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorName;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("kind", kind)
                .append("attributes", attributes)
                .append("enabled", enabled)
                .append("connectorUuid", connectorUuid)
                .append("connectorName", connectorName)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

package com.otilm.api.model.client.discovery;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class DiscoveryDto {
    @Schema(description = "Discovery name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "List of Attributes for Discovery", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;
    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;
    @Schema(description = "Discovery Provider UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;
    @Schema(description = "Discovery Kind", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;
    @Schema(description = "List of triggers to be triggered after the discovery is finished, triggers will be evaluated in given order")
    private List<UUID> triggers;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("connectorUuid", connectorUuid)
                .append("kind", kind)
                .toString();
    }
}

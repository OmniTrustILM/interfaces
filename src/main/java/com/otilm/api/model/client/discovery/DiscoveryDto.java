package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Schema(description = "Discovery interface of the Connector to run through. Required when the Connector has more "
            + "than one, selected automatically when it has one; a Connector with none runs a v1 discovery.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID interfaceUuid;

    @Schema(description = "Discovery kind. Required for a v1 Discovery Provider; omit it for a v2 one, which has no "
            + "kinds.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @Schema(description = "Triggers to evaluate, in this order, when the discovery finishes")
    private List<UUID> triggers;

    // @Size skips null, so only an explicit empty list fails here. The rules that depend on the connector live in
    // Core, where it is known.
    @Size(min = 1, message = "resources must name at least one resource type when supplied")
    @ArraySchema(minItems = 1,
            arraySchema = @Schema(
                    description = "Resource types to discover, as resource codes. Required for a v2 Discovery "
                            + "Provider; omit it for a v1 one, which discovers certificates and refuses the field.",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<@NotNull(message = "resources must not contain a null resource type") Resource> resources;

    @Schema(description = "Per-resource attributes, keyed by resource code, against the definitions the Connector "
            + "advertises per resource. A resource with no entry is still discovered, unrefined: resources alone "
            + "decides what the run targets. Omit for a v1 Discovery Provider.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, propertyNames = Resource.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Resource, List<RequestAttribute>> resourceAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("connectorUuid", connectorUuid)
                .append("interfaceUuid", interfaceUuid)
                .append("kind", kind)
                .append("resources", resources)
                // resourceAttributes left out: it repeats the attribute payload once per resource.
                .toString();
    }
}

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

    @Schema(description = "UUID of the DISCOVERY Connector Interface to bind this run to. Required when the "
            + "Connector exposes more than one; selected automatically when it exposes exactly one. A "
            + "Connector with no DISCOVERY interface yields a legacy v1 run.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID interfaceUuid;

    @Schema(description = "Discovery Kind", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;
    @Schema(description = "List of triggers to be triggered after the discovery is finished, triggers will be evaluated in given order")
    private List<UUID> triggers;

    // @Size skips null by jakarta semantics, so an omitted field never fires it; only an explicit empty
    // list does. The connector-dependent rules (a v2 connector requires the field, a v1 connector refuses
    // it) stay at Core's service boundary, where the connector is known.
    @Size(min = 1, message = "resources must name at least one resource type when supplied")
    @ArraySchema(minItems = 1,
            arraySchema = @Schema(
                    description = "Resource types this run should discover, as resource wire codes (e.g. "
                            + "\"certificates\", \"keys\"). Omit only for a run against a v1 connector, which "
                            + "keeps exactly today's semantics: the run discovers certificates. A v2 connector "
                            + "requires the field — Core rejects a request that omits it — and a v1 connector "
                            + "rejects a request that supplies it: discovery has no resource dimension in v1.",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<@NotNull(message = "resources must not contain a null resource type") Resource> resources;

    @Schema(description = "Per-resource attributes, keyed by resource wire code, collected against the "
            + "per-resource attribute definitions the connector advertises. Omit for exactly "
            + "today's v1 semantics: only the run-level attributes above are sent to the "
            + "connector. A key absent from this map means that resource is discovered with no "
            + "refinement of its own, not that it is excluded — resources alone decides what a run targets.",
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
                // resourceAttributes is deliberately not appended: it multiplies the attribute payload
                // by the number of targeted resources, and the run-level attributes already above give
                // a log reader the shape of what was submitted.
                .toString();
    }
}

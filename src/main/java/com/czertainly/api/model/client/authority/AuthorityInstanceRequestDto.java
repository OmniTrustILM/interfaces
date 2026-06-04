package com.czertainly.api.model.client.authority;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class AuthorityInstanceRequestDto {

    @Schema(description = "Authority instance name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Authority instance name is required")
    private String name;

    @Schema(description = "List of Authority instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "attributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "UUID of Authority provider",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "connectorUuid is required")
    private String connectorUuid;

    @Schema(description = "UUID of the Connector Interface (AUTHORITY) to bind this instance to. "
            + "Required to select a specific version when the connector exposes more than one "
            + "AUTHORITY interface (e.g. v2 and v3 side-by-side). "
            + "When null, the authority targets a legacy v1 connector that declares no connector "
            + "interface; if such a connector exposes exactly one AUTHORITY interface it is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID interfaceUuid;

    @Schema(description = "Authority instance Kind. Used only by v1 connectors (framework V1), which "
            + "implement the legacy AttributesController and carry kind in the wire path. v2 connectors "
            + "ignore it (they dispatch on connector interface).",
            examples = {"LegacyEjbca", "ADCS"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kind;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("attributes", attributes)
                .append("connectorUuid", connectorUuid)
                .append("interfaceUuid", interfaceUuid)
                .append("kind", kind)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

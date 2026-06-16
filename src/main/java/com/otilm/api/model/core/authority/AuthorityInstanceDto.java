package com.otilm.api.model.core.authority;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AuthorityInstanceDto extends NameAndUuidDto {

    @Schema(description = "List of Authority instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Status of Authority instance",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "Connector (Authority provider) this instance belongs to.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto connector;

    @Schema(description = "Connector Interface this Authority instance is bound to; null for legacy v1 connectors, "
            + "which are identified by kind instead.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private ConnectorInterfaceDto connectorInterface;

    /**
     * @deprecated use {@link #connector} instead.
     */
    @Schema(description = "UUID of Authority provider. Deprecated — use connector.uuid instead.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true)
    @Deprecated(forRemoval = true)
    private String connectorUuid;

    /**
     * @deprecated use {@link #connector} instead.
     */
    @Schema(description = "Name of Authority provider. Deprecated — use connector.name instead.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true)
    @Deprecated(forRemoval = true)
    private String connectorName;

    @Schema(description = "Authority Instance Kind",
            examples = {"LegacyEjbca, ADCS, etc."},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("status", status)
                .append("connector", connector)
                .append("connectorInterface", connectorInterface)
                .append("kind", kind)
                .toString();
    }
}

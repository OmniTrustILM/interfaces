package com.otilm.api.model.core.authority;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class AuthorityInstanceDto extends NameAndUuidDto {

    @Schema(description = "List of Authority instance Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Status of Authority instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "Connector (Authority provider) this instance belongs to",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto connector;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto. nullable rides along
    // but OpenAPI 3.0 only honours it where a type sits in the same schema object, so the field states its own
    // nullability in prose. The key is serialized as an explicit null rather than omitted.
    @Schema(description = "The connector interface this authority instance is bound to. Sent as an explicit null "
            + "for a legacy v1 connector, which is identified by kind instead; the key is always present.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true,
            schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    private ConnectorInterfaceDto connectorInterface;

    /**
     * @deprecated use {@link #connector} instead.
     */
    @Schema(description = "UUID of Authority provider; deprecated, use connector.uuid instead",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true)
    @Deprecated(forRemoval = true)
    private String connectorUuid;

    /**
     * @deprecated use {@link #connector} instead.
     */
    @Schema(description = "Name of Authority provider; deprecated, use connector.name instead",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true)
    @Deprecated(forRemoval = true)
    private String connectorName;

    @Schema(description = "Authority instance kind; present for legacy v1 connectors, null for v2/v3 authorities "
            + "which are identified by connectorInterface", examples = {"LegacyEjbca", "ADCS"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
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

package com.otilm.api.model.core.vault;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VaultInstanceDto extends NameAndUuidDto {

    @Schema(description = "Description of the Vault instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Connector associated with this Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto connector;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto. nullable is inert
    // beside an allOf, so the field states its own nullability in prose.
    @Schema(description = "The connector interface this vault instance is associated with. Sent as an explicit null "
            + "once the interface has been removed from its connector; the key is always present.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true,
            schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    private ConnectorInterfaceDto connectorInterface;

}

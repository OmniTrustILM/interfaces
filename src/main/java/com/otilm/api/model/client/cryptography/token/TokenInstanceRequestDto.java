package com.otilm.api.model.client.cryptography.token;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenInstanceRequestDto {
    @Schema(description = "Name of the Token Instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Token Instance description")
    private String description;

    @Schema(description = "UUID of the Connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Connector kind. Required for connectors based on version 1 of the connector framework;"
            + " omitted for connectors based on version 2 (NG) of the connector framework.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kind;

    @Schema(description = "Custom Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> customAttributes;

    @Schema(description = "Attributes for Token Instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;
}

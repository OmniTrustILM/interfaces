package com.otilm.api.model.client.cryptography.token;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenInstanceRequestDto {
    @Schema(
            description = "Name of the Token Instance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Token Instance description"
    )
    private String description;

    @Schema(
            description = "UUID of the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String connectorUuid;

    @Schema(
            description = "Connector Kind",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String kind;

    @Schema(
            description = "Custom Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> customAttributes;

    @Schema(
            description = "Attributes for Token Instance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> attributes;
}

package com.otilm.api.model.client.cryptography.token.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(name = "TokenInstanceRequestDtoV2", description = "Core-owned token configuration for a cryptography provider v2 connector")
public class TokenInstanceRequestDto {

    @NotBlank
    @Schema(description = "Token instance name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Token instance description")
    private String description;

    @NotNull
    @Schema(description = "Cryptography provider connector UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID connectorUuid;

    @NotNull
    @Schema(description = "Connector-defined token attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> tokenAttributes;

    @NotNull
    @Schema(description = "Core custom attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> customAttributes;
}

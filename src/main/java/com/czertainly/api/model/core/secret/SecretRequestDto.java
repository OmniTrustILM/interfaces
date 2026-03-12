package com.czertainly.api.model.core.secret;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.connector.secrets.content.SecretContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SecretRequestDto {

    @Schema(description = "Name of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "Description of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Content of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private SecretContent secret;

    @Schema(description = "List of attributes associated with the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of custom attributes associated with the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();

}

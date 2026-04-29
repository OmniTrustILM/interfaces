package com.otilm.api.model.core.secret;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.secrets.content.SecretContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SecretUpdateRequestDto {

    @Schema(description = "Description of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Content of the secret. If not provided, the secret content will not be updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SecretContent secret;

    @Schema(description = "List of attributes associated with the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of custom attributes associated with the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}

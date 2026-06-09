package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "ApiKeySecretContent",
        description = "Secret representing an API Key"
)
public class ApiKeySecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(description = "API Key content string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public ApiKeySecretContent() {
        super(SecretType.API_KEY);
    }

    public ApiKeySecretContent(String content) {
        this();
        this.content = content;
    }
}

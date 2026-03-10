package com.czertainly.api.model.connector.secrets.content;

import com.czertainly.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "PrivateKeySecretContent",
        description = "Secret representing private key"
)
public class PrivateKeySecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(description = "BASE64 encoded content of key in PEM format", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public PrivateKeySecretContent() {
        super(SecretType.PRIVATE_KEY);
    }

    public PrivateKeySecretContent(String content) {
        this();
        this.content = content;
    }
}

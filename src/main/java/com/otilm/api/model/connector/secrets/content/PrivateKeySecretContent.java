package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "PrivateKeySecretContent", description = "Secret representing private key")
public class PrivateKeySecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(description = "BASE64 encoded content of key in PEM format", requiredMode = Schema.RequiredMode.REQUIRED)
    @Sensitive
    private String content;

    public PrivateKeySecretContent() {
        super(SecretType.PRIVATE_KEY);
    }

    public PrivateKeySecretContent(String content) {
        this();
        this.content = content;
    }
}

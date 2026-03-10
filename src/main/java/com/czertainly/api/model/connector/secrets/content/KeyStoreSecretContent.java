package com.czertainly.api.model.connector.secrets.content;

import com.czertainly.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "KeyStoreSecretContent",
        description = "Secret representing Key Store"
)
public class KeyStoreSecretContent extends SecretContent {

    @NotNull
    @Schema(description = "Key Store type", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyStoreType keyStoreType;

    @NotBlank
    @ToString.Exclude
    @Schema(description = "BASE64 encoded content of key store", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull
    @ToString.Exclude
    @Schema(description = "Password for key store", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public KeyStoreSecretContent() {
        super(SecretType.KEY_STORE);
    }

    public KeyStoreSecretContent(KeyStoreType keyStoreType, String content, String password) {
        this();
        this.keyStoreType = keyStoreType;
        this.content = content;
        this.password = password;
    }
}

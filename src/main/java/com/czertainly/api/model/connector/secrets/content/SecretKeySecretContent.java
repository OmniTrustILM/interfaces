package com.czertainly.api.model.connector.secrets.content;

import com.czertainly.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "SecretKeySecretContent",
        description = "Secret representing secret key"
)
public class SecretKeySecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(description = "BASE64 encoded binary (raw) content of key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public SecretKeySecretContent() {
        super(SecretType.SECRET_KEY);
    }

    public SecretKeySecretContent(String content) {
        this();
        this.content = content;
    }
}

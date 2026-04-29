package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "BasicAuthSecretContent",
        description = "Secret representing Basic Authentication credentials"
)
public class BasicAuthSecretContent extends SecretContent {

    @NotBlank
    @Schema(description = "Username for Basic Authentication", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @ToString.Exclude
    @Schema(description = "Password for Basic Authentication", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public BasicAuthSecretContent() {
        super(SecretType.BASIC_AUTH);
    }

    public BasicAuthSecretContent(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }
}

package com.czertainly.api.model.connector.secrets.content;

import com.czertainly.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "GenericSecretContent",
        description = "Secret representing generic content represented as string"
)
public class GenericSecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(description = "Generic secret content represented as string. In case secret content is binary data, it should be encoded as BASE64 string.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public GenericSecretContent() {
        super(SecretType.GENERIC);
    }

    public GenericSecretContent(String content) {
        this();
        this.content = content;
    }
}

package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "JwtTokenSecretContent",
        description = "Secret representing JWT Token"
)
public class JwtTokenSecretContent extends SecretContent {

    @NotBlank
    @ToString.Exclude
    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "JWT Token content in compact (dot-separated) format specified in [RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519#section-3)",
            example = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
    )
    private String content;

    public JwtTokenSecretContent() {
        super(SecretType.JWT_TOKEN);
    }

    public JwtTokenSecretContent(String content) {
        this();
        this.content = content;
    }
}

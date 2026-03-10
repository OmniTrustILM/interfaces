package com.czertainly.api.model.connector.secrets.content;

import com.czertainly.api.model.connector.secrets.SecretType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicAuthSecretContent.class, name = SecretType.Codes.BASIC_AUTH),
        @JsonSubTypes.Type(value = ApiKeySecretContent.class, name = SecretType.Codes.API_KEY),
        @JsonSubTypes.Type(value = JwtTokenSecretContent.class, name = SecretType.Codes.JWT_TOKEN),
        @JsonSubTypes.Type(value = PrivateKeySecretContent.class, name = SecretType.Codes.PRIVATE_KEY),
        @JsonSubTypes.Type(value = SecretKeySecretContent.class, name = SecretType.Codes.SECRET_KEY),
        @JsonSubTypes.Type(value = KeyStoreSecretContent.class, name = SecretType.Codes.KEY_STORE),
        @JsonSubTypes.Type(value = KeyValueSecretContent.class, name = SecretType.Codes.KEY_VALUE),
        @JsonSubTypes.Type(value = GenericSecretContent.class, name = SecretType.Codes.GENERIC)
})
@Schema(implementation = SecretContentDto.class)
public abstract class SecretContent implements SecretContentDto {

    @NotNull
    @Schema(description = "Secret type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SecretType.Codes.API_KEY})
    private final SecretType type;

    protected SecretContent() {
        this.type = null; // Type will be set by subclasses
    }

    protected SecretContent(SecretType type) {
        this.type = type;
    }
}

package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "SecretContent",
        description = "Secret content dependent on secret type",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SecretType.Codes.BASIC_AUTH, schema = BasicAuthSecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.API_KEY, schema = ApiKeySecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.JWT_TOKEN, schema = JwtTokenSecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.PRIVATE_KEY, schema = PrivateKeySecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.SECRET_KEY, schema = SecretKeySecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.KEY_STORE, schema = KeyStoreSecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.KEY_VALUE, schema = KeyValueSecretContent.class),
                @DiscriminatorMapping(value = SecretType.Codes.GENERIC, schema = GenericSecretContent.class),
        },
        oneOf = {
                BasicAuthSecretContent.class,
                ApiKeySecretContent.class,
                JwtTokenSecretContent.class,
                PrivateKeySecretContent.class,
                SecretKeySecretContent.class,
                KeyStoreSecretContent.class,
                KeyValueSecretContent.class,
                GenericSecretContent.class
        })
public interface SecretContentDto extends Serializable {

    @Schema(description = "Secret type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SecretType.Codes.API_KEY})
    SecretType getType();
}

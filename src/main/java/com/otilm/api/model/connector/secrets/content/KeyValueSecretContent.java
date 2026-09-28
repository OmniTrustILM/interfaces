package com.otilm.api.model.connector.secrets.content;

import com.otilm.api.model.connector.secrets.SecretType;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "KeyValueSecretContent", description = "Secret representing key-value pairs")
public class KeyValueSecretContent extends SecretContent {

    @NotNull
    @ToString.Exclude
    @Schema(description = "Key-Value pairs stored as the secret content, represented by JSON object",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Sensitive
    private Map<String, Object> content;

    public KeyValueSecretContent() {
        super(SecretType.KEY_VALUE);
    }

    public KeyValueSecretContent(Map<String, Object> content) {
        this();
        this.content = content;
    }
}

package com.otilm.api.model.core.settings.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class JwkDto implements Serializable {

        @Schema(description = "The key ID parameter used to match a specific key in provider", requiredMode = Schema.RequiredMode.REQUIRED)
        private String kid;

        @Schema(description = "Base64 encoded value of public key", requiredMode = Schema.RequiredMode.REQUIRED)
        private String publicKey;

        @Schema(description = "The cryptographic algorithm family used with the key, such as \"RSA\" or \"EC\"", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String keyType;

        @Schema(description = "The algorithm intended for use with the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String algorithm;

        @Schema(description = "The intended use of the public key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String use;

}

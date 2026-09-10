package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "A freshly generated External Account Binding HMAC key. The platform does not store it - "
        + "store it in a secret and register that secret's UUID on an ACME Profile to put the key into use.")
public class AcmeEabKeyDto {

    @ToString.Exclude
    @Schema(description = "Base64url-encoded HMAC key, 256 bits of randomness. This is the value an ACME client "
            + "MACs its External Account Binding with, and the value to store as the secret's content.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String key;
}

package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "External Account Binding (RFC 8555 section 7.3.4): a JWS in flattened JSON serialization whose "
        + "protected header names the binding key (kid) and the newAccount URL, whose payload is the account's "
        + "public JWK, and whose signature is a MAC under the externally provisioned key.")
public class ExternalAccountBinding {

    @JsonProperty("protected")
    @Schema(description = "Base64url-encoded protected header: {\"alg\":\"HS256\",\"kid\":<key identifier>,"
            + "\"url\":<newAccount URL>}. The kid is the UUID of the secret holding the binding key on the "
            + "ACME Profile.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String protectedHeader;

    @Schema(description = "Base64url-encoded payload: the JWK of the account key being registered",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String payload;

    @ToString.Exclude
    @Schema(description = "Base64url-encoded HMAC over the protected header and payload under the binding key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String signature;
}

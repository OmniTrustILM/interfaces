package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Context for V2 cryptography request bodies that target a token and token profile.
 */
@Getter
@Setter
public class TokenProfileScopedRequestV2Dto extends TokenScopedRequestV2Dto {

    @Schema(description = "Token profile attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "tokenProfileAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> tokenProfileAttributes;

    @Schema(description = "Key usages selected on the token profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyUsages must contain at least one usage")
    private Set<@NotNull(message = "keyUsages must not contain null entries") KeyUsage> keyUsages;

}

package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Context for V2 cryptography request bodies that target a token and token profile.
 */
@Getter
@Setter
@Schema(name = "TokenProfileScopedRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class TokenProfileScopedRequestV2Dto extends TokenScopedRequestV2Dto {

    @Schema(description = "Token profile attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "tokenProfileAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(
            message = "tokenProfileAttributes must not contain null entries") RequestAttribute> tokenProfileAttributes;
}

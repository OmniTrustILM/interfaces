package com.otilm.api.model.core.cryptography.token;

import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceStatusComponent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenInstanceStatusDetailDto {
    @Schema(
            description = "Token instance status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TokenInstanceStatus status;

    @Schema(
            description = "Components of the Token instance status"
    )
    private Map<String, TokenInstanceStatusComponent> components;

    public TokenInstanceStatusDetailDto(TokenInstanceStatus status) {
        this.status = status;
    }
}

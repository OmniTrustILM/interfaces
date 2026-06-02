package com.czertainly.api.model.connector.secrets;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Schema(name = "SecretVerificationResponseDto",
        description = "Result of a secret verification. Indicates whether the candidate value matched the stored secret.")
public class SecretVerificationResponseDto {

    @Schema(description = "True if the candidate matches the stored secret content", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean match;
}

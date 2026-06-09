package com.otilm.api.model.connector.cryptography.token;

import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenInstanceStatusDto {

    @Schema(description = "Token instance status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenInstanceStatus status;

    @Schema(description = "Components of the Token instance status")
    private Map<String, TokenInstanceStatusComponent> components;

}

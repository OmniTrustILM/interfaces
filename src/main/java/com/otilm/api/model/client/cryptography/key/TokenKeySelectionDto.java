package com.otilm.api.model.client.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TokenKeySelectionDto", description = "A key pair to onboard, and the name to give it")
public class TokenKeySelectionDto {

    public static final int MAX_HANDLE_LENGTH = 4096;

    @Schema(description = "Handle of the key pair, exactly as the listing returned it",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_HANDLE_LENGTH)
    @NotBlank(message = "keyPairHandle is required")
    @Size(max = MAX_HANDLE_LENGTH, message = "keyPairHandle must not exceed " + MAX_HANDLE_LENGTH + " characters")
    private String keyPairHandle;

    @Schema(description = "Name of the onboarded key, by default the name the listing suggested. A key pair "
            + "onboarded into an existing key keeps the name of that key.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;
}

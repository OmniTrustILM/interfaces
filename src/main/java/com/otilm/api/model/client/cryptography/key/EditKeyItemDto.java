package com.otilm.api.model.client.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditKeyItemDto {

    @Schema(description = "Name of the key item", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String name;
}

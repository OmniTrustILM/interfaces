package com.czertainly.api.model.connector.secrets;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class SecretRequestDto {

    @NotBlank
    @Schema(description = "Name of the secret", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"MyServerCredentials"})
    private String name;

    @NotNull
    @Schema(description = "Secret type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SecretType.Codes.API_KEY})
    private SecretType type;

    @Builder.Default
    @Schema(description = "Vault attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> vaultAttributes = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Secret attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> secretAttributes = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Metadata for the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> metadata = new ArrayList<>();
}

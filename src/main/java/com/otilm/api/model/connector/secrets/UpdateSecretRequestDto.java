package com.otilm.api.model.connector.secrets;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.secrets.content.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
public class UpdateSecretRequestDto implements SecretOperationRequest {

    @NotBlank
    @Schema(description = "Name of the secret", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"MyServerCredentials"})
    private String name;

    @Valid
    @NotNull
    @Schema(description = "Secret content", requiredMode = Schema.RequiredMode.REQUIRED)
    private SecretContent secret;

    @Builder.Default
    @Schema(description = "Vault attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> vaultAttributes = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Vault profile attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> vaultProfileAttributes = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Secret attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> secretAttributes = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Metadata for the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> metadata = new ArrayList<>();
}

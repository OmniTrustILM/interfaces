package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
@Schema(name = "TokenKeySelectionDto", description = "A key to onboard, and the name to give it")
public class TokenKeySelectionDto {

    @Schema(description = "Metadata of the key pair, exactly as the listing returned it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyPairMeta is required and must not be empty")
    private List<@NotNull(
            message = "keyPairMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyPairMeta;

    @Schema(description = "Name of the onboarded key, by default the name the listing suggested. A key pair "
            + "onboarded into an existing key keeps the name of that key.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;
}

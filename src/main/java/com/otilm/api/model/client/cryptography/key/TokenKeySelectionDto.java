package com.otilm.api.model.client.cryptography.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.cryptography.v2.key.KeyMetaMatcher;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
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

    /** An ambiguous handle matches no listed key pair, so its selection could only fail. */
    @AssertTrue(message = "keyPairMeta must name each attribute once, and each content item must carry data")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isKeyPairMetaUnambiguous() {
        if (keyPairMeta == null || keyPairMeta.stream().anyMatch(Objects::isNull)) {
            return true;
        }
        return KeyMetaMatcher.isUnambiguous(keyPairMeta);
    }
}

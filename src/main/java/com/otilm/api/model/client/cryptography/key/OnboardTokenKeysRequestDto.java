package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
@Schema(name = "OnboardTokenKeysRequestDto",
        description = "Key pairs found in the key storage to onboard into the inventory")
public class OnboardTokenKeysRequestDto implements Loggable {

    @Schema(description = "Key pairs to onboard", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keys must name at least one key pair")
    private List<@NotNull(message = "keys must not contain null items") @Valid TokenKeySelectionDto> keys;

    @Schema(description = "Custom attributes of each new key. A key pair onboarded into an existing key keeps the "
            + "attributes of that key.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes;

    @Override
    public Serializable toLogData() {
        return null;
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return keys == null
                ? List.of()
                : keys.stream().map(TokenKeySelectionDto::getName).filter(Objects::nonNull).toList();
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of();
    }
}

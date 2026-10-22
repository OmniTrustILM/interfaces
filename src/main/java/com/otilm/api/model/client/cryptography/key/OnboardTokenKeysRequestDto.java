package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(name = "OnboardTokenKeysRequestDto",
        description = "Keys found in the key storage to onboard into the inventory")
public class OnboardTokenKeysRequestDto {

    @Schema(description = "Keys to onboard.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keys must name at least one key")
    @Size(max = ListKeysRequestV2Dto.MAX_LIMIT,
            message = "keys must contain at most " + ListKeysRequestV2Dto.MAX_LIMIT + " keys")
    private List<@NotNull(message = "keys must not contain null items") @Valid TokenKeySelectionDto> keys;

    @Schema(description = "Custom attributes of each new key. A key pair onboarded into an existing key keeps the "
            + "attributes of that key.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes;
}

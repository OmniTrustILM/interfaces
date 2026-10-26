package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(name = "ListKeysResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ListKeysResponseV2Dto {

    @Schema(description = "Keys of the requested page, each of the requested key request type",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "items is required")
    private List<@NotNull(message = "items must not contain null items") @Valid ListedKeyV2Dto> items;

    @Schema(description = "Keys on this page that cannot be offered for onboarding, each with its reason",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull(message = "unusableKeys must not contain null items") @Valid UnusableKeyV2Dto> unusableKeys;

    @Schema(description = "Number of keys in the whole listing. Each appears once on its page, as a listed or an "
            + "unusable key.", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "totalItems is required")
    @Min(value = 0, message = "totalItems must not be negative")
    private Long totalItems;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key listing property: " + property);
    }
}

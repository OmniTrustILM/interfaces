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

    @Schema(description = "Keys of the requested page without an identifier unique to them. Each counts as one key "
            + "toward `limit` and `totalItems`. Only an unfiltered listing returns them.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull(
            message = "unaddressableKeys must not contain null items") @Valid UnaddressableKeyV2Dto> unaddressableKeys;

    @Schema(description = "Number of keys in the whole listing, after `keyMetaFilter`. Keys the connector leaves out "
            + "are included in the count.", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "totalItems is required")
    @Min(value = 0, message = "totalItems must not be negative")
    private Long totalItems;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key listing property: " + property);
    }
}

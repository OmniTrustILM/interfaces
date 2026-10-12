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
@Schema(name = "ListKeyPairsResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ListKeyPairsResponseV2Dto {

    @Schema(description = "Key pairs of the requested page", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "items is required")
    private List<@NotNull(message = "items must not contain null items") @Valid ListedKeyPairV2Dto> items;

    @Schema(description = "Keys of the requested page without an identifier unique to them. Each counts as one key "
            + "pair toward `limit` and `totalItems`. Only an unfiltered listing returns them.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull(
            message = "unaddressableKeys must not contain null items") @Valid UnaddressableKeyV2Dto> unaddressableKeys;

    @Schema(description = "Number of key pairs in the whole listing, after `keyPairMetaFilter`",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "totalItems is required")
    @Min(value = 0, message = "totalItems must not be negative")
    private Long totalItems;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key listing property: " + property);
    }
}

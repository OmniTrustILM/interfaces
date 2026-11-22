package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
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

    @Schema(description = "Entries of the requested page, one per key, in the token's order; the status "
            + "`invalidHandle` is reserved for resolutions", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "items is required")
    private List<@NotNull(message = "items must not contain null items") @Valid ListedKeyEntryV2Dto> items;

    @Schema(description = "Number of keys in the whole listing; each appears once, on its page",
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

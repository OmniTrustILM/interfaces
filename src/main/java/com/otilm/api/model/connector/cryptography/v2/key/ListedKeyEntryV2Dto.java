package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.ToString;

/** One entry of a key listing, in the token's order. */
@ToString(callSuper = true)
@Schema(name = "ListedKeyEntryV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class ListedKeyEntryV2Dto extends KeyEntryV2Dto {

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "status must not be invalidHandle. invalidHandle is reported only by resolution")
    public boolean isStatusOfAListedKey() {
        return getStatus() != KeyEntryStatus.INVALID_HANDLE;
    }
}

package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Status response for asynchronous key-pair creation. */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "KeyPairOperationStatusResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyPairOperationStatusResponseV2Dto extends KeyCreationStatusResponseV2Dto {

    @Override
    @NotNull(message = "keyRequestType is required")
    @Schema(description = "Type of key requested", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = KeyRequestType.Codes.KEY_PAIR)
    public KeyRequestType getKeyRequestType() {
        return KeyRequestType.KEY_PAIR;
    }

    @Valid
    @ConvertGroup(to = SynchronousResponse.class)
    @Schema(description = "Created key-pair result. Present only when status is `completed`.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyPairDataResponseV2Dto result;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "result is required when status is completed and must be absent otherwise")
    public boolean isResultConsistentWithStatus() {
        return super.isResultConsistentWithStatus(result);
    }
}

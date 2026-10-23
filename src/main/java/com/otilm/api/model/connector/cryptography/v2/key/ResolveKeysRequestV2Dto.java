package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ResolveKeysRequestV2Dto",
        description = "The handles of keys to resolve. Each is answered once, under its identifier.",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ResolveKeysRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    public static final int MAX_KEYS = 1000;

    @Schema(description = "Type of the keys the handles name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    @Schema(description = "Handles to resolve, each under an identifier unique in the request",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keys is required")
    @Size(min = 1, max = MAX_KEYS, message = "keys must hold 1 to " + MAX_KEYS + " handles")
    @UniqueIdentifiers
    private List<@NotNull(message = "keys must not contain null items") @Valid KeyHandleV2Dto> keys;

    /** Backs {@code builder()}, since a class-level builder covers only the fields declared here. */
    @Builder
    private static ResolveKeysRequestV2Dto inScope(TokenProfileScopedRequestV2Dto scope, KeyRequestType keyRequestType,
            List<KeyHandleV2Dto> keys) {
        ResolveKeysRequestV2Dto dto = new ResolveKeysRequestV2Dto();
        dto.setTokenAttributes(scope.getTokenAttributes());
        dto.setTokenProfileAttributes(scope.getTokenProfileAttributes());
        dto.setKeyRequestType(keyRequestType);
        dto.setKeys(keys);
        return dto;
    }
}

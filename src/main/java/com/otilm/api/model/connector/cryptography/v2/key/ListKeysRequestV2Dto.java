package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(name = "ListKeysRequestV2Dto", description = """
        The page of the token's keys of one type to return. The connector keeps their order stable for an unchanged
        token. `keyMetaFilter` applies before `offset` and `limit`.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ListKeysRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    public static final int MAX_LIMIT = 1000;

    @Schema(description = "Type of the keys to list. The connector must refuse a type it cannot list with "
            + "`KEY_TYPE_NOT_LISTABLE`.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    @Schema(description = "Number of keys to skip", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "offset is required")
    @Min(value = 0, message = "offset must not be negative")
    private Integer offset;

    @Schema(description = "Maximum number of keys to return", requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1", maximum = "" + MAX_LIMIT)
    @NotNull(message = "limit is required")
    @Min(value = 1, message = "limit must be positive")
    @Max(value = MAX_LIMIT, message = "limit must not exceed " + MAX_LIMIT)
    private Integer limit;

    @Schema(description = "Handles of the keys to list, each exactly as an earlier listing returned it: the "
            + "`keyPairMeta` of a key pair or the `keyMeta` of a secret key. A returned handle matches one holding the "
            + "same metadata names in any order, each with the same data. An absent filter lists every key.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, message = "keyMetaFilter must name at least one key when present")
    private List<@NotEmpty(message = "keyMetaFilter must not contain an empty handle") List<@NotNull(
            message = "keyMetaFilter must not contain null items") @ValidMetadataAttribute MetadataAttribute>> keyMetaFilter;

    /** Backs {@code builder()}, since a class-level builder covers only the fields declared here. */
    @Builder
    private static ListKeysRequestV2Dto inScope(TokenProfileScopedRequestV2Dto scope, KeyRequestType keyRequestType,
            Integer offset, Integer limit, List<List<MetadataAttribute>> keyMetaFilter) {
        ListKeysRequestV2Dto dto = new ListKeysRequestV2Dto();
        dto.setTokenAttributes(scope.getTokenAttributes());
        dto.setTokenProfileAttributes(scope.getTokenProfileAttributes());
        dto.setKeyRequestType(keyRequestType);
        dto.setOffset(offset);
        dto.setLimit(limit);
        dto.setKeyMetaFilter(keyMetaFilter);
        return dto;
    }
}

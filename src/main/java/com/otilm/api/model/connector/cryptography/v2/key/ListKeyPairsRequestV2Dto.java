package com.otilm.api.model.connector.cryptography.v2.key;

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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ListKeyPairsRequestV2Dto", description = """
        The page of the token's key pairs to return. The connector keeps their order stable for an unchanged token.
        `keyPairMetaFilter` applies before `offset` and `limit`.

        The listing covers key pairs only.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ListKeyPairsRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    public static final int MAX_LIMIT = 1000;

    @Schema(description = "Number of key pairs to skip", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "offset is required")
    @Min(value = 0, message = "offset must not be negative")
    private Integer offset;

    @Schema(description = "Largest number of key pairs to return", requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1", maximum = "" + MAX_LIMIT)
    @NotNull(message = "limit is required")
    @Min(value = 1, message = "limit must be positive")
    @Max(value = MAX_LIMIT, message = "limit must not exceed " + MAX_LIMIT)
    private Integer limit;

    @Schema(description = "Handles of the key pairs to list, each a `keyPairMeta` exactly as an earlier listing "
            + "returned it. A returned `keyPairMeta` matches a handle holding the same metadata names in any order, "
            + "each with the same data. An absent filter lists every key pair.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, message = "keyPairMetaFilter must name at least one key pair when present")
    private List<@NotEmpty(message = "keyPairMetaFilter must not contain an empty handle") List<@NotNull(
            message = "keyPairMetaFilter must not contain null items") @ValidMetadataAttribute MetadataAttribute>> keyPairMetaFilter;
}

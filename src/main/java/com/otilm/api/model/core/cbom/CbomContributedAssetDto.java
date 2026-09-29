package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * One inventory asset as one CBOM record contributed it: the inventory row, plus the {@code bom-ref} values of the
 * document's components that were folded into that row. A client holding the document maps each component to its
 * inventory asset through these values; they play no part in how assets are deduplicated.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CbomContributedAssetDto extends CryptographicAssetDto {

    @Schema(description = "The bom-ref values of this document's components that were folded into this asset, in "
            + "document order. Navigation data only. A component links to no asset when its bom-ref is not a string "
            + "or is defined more than once in the document; when the value cannot be stored, because it has no "
            + "valid text encoding, contains a NUL character or is longer than 1024 characters; or when it comes "
            + "after the first 256 values kept per asset and document. Empty when no component of this document "
            + "could be linked.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> bomRefs = new ArrayList<>();
}

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

    @Schema(description = "The bom-ref values of this document's components folded into this asset, in document "
            + "order, at most 256. A value that is empty, not a string, not well-formed Unicode, contains NUL or is "
            + "longer than 1024 characters is left out, and the asset is still listed. A document that repeats a "
            + "bom-ref is not synced.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> bomRefs = new ArrayList<>();
}

package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

/**
 * The inventory asset whose own PQC verdict another asset's verdict was carried over from.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "An inventory asset whose stored verdict was carried over into another asset's: the key a "
        + "certificate certifies or the algorithm it is signed with, whichever is weaker, or the weakest algorithm a "
        + "protocol's cipher suites name. The uuid is the one recorded when the verdict was decided; visible says "
        + "whether the reader may read that asset's detail now. name and type are read from it as currently stored, "
        + "and are absent when visible is false; type is also absent for an asset declaring no CycloneDX asset type, "
        + "and name for one with no name to serve")
public class PqcReferencedAssetDto {

    @Schema(description = "UUID of the referenced inventory asset, as recorded when the verdict was decided",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "True when the referenced asset is still in the inventory and the reader may read its "
            + "detail; false when it has been removed or the reader's role does not grant detail access to it. When "
            + "false, name and type are absent", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean visible;

    @Schema(description = "Display name of the referenced asset, so a link can be labelled without a second read; "
            + "absent when visible is false, or when the asset has no name to serve",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Type of the referenced asset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetType type;
}

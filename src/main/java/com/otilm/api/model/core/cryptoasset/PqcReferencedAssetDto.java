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
@Schema(description = "An inventory asset whose own verdict was carried over into another asset's: the key a "
        + "certificate certifies, or the weakest algorithm a protocol's cipher suites name. The uuid is the one recorded "
        + "when the verdict was decided; name and type are read from that asset as currently stored, and are absent "
        + "when it is no longer in the inventory or not visible to the reader")
public class PqcReferencedAssetDto {

    @Schema(description = "UUID of the referenced inventory asset, as recorded when the verdict was decided",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Display name of the referenced asset, so a link can be labelled without a second read; "
            + "absent when the asset is not visible to the reader", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Type of the referenced asset; absent when the asset is not visible to the reader",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetType type;
}

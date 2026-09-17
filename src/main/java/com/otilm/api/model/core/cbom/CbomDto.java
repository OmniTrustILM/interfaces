package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.core.search.AttributeProjectable;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class CbomDto implements AttributeProjectable {

    /** Shared by every member that carries a failure reason: what the text is, and what it is never. */
    public static final String OPERATOR_WORDED_REASON = "in words meant for an operator; never a raw driver or parser "
            + "message";

    @Schema(description = "UUID of a CBOM record", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Creation timestamp of CBOM database record", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "CBOM serial number (URN)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "CBOM version", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "CBOM spec version", requiredMode = Schema.RequiredMode.REQUIRED)
    private String specVersion;

    @Schema(description = "A timestamp from CBOM metadata", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime timestamp;

    @Schema(description = "CBOM source (e.g.: CBOM-Lens)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String source;

    @Schema(description = "Number of algorithms", requiredMode = Schema.RequiredMode.REQUIRED)
    private int algorithms;

    @Schema(description = "Number of certificates", requiredMode = Schema.RequiredMode.REQUIRED)
    private int certificates;

    @Schema(description = "Number of protocols", requiredMode = Schema.RequiredMode.REQUIRED)
    private int protocols;

    @Schema(description = "Number of crypto material items", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cryptoMaterial;

    @Schema(description = "Total number of assets", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalAssets;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "State of the cryptographic asset sync for this CBOM record; pending until the first sync "
            + "attempt considers it. Platform versions predating the asset sync omit the field.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CbomAssetSyncState assetSyncState;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "When this record last successfully reached the synced state; later failed attempts "
            + "neither advance nor clear it. Platform versions predating the asset sync omit the field.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime assetSyncedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Why the last cryptographic asset sync attempt for this record failed or refused the "
            + "document, " + OPERATOR_WORDED_REASON + ". A later attempt that succeeds clears it. Absent while there "
            + "is no failure to report, and on platform versions predating it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String assetSyncError;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    // No example: the platform registers no custom, metadata or data attributes against this resource, so the
    // shared one - which shows custom values - documents a payload this listing cannot produce.
    @Schema(description = AttributeProjectable.ATTRIBUTE_VALUES_DESCRIPTION,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<FilterFieldSource, Map<String, List<BaseAttributeContentV3<?>>>> attributeValues;
}

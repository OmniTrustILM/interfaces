package com.otilm.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CbomSyncedEventData implements EventData {

    @Schema(description = "CBOM UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cbomUuid;

    @Schema(description = "CBOM serial number", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "CBOM version", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "CBOM source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String source;

    @Schema(description = "Total assets declared by the CBOM document", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalAssets;

    @Schema(description = "Time the cryptographic assets were ingested", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime assetsSyncedAt;
}

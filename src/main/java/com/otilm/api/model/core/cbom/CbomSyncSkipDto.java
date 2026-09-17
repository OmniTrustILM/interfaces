package com.otilm.api.model.core.cbom;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * One CBOM Repository entry the header sync could not store: which document, how far its bounded retry got, and why the
 * last attempt failed. Every member is always present: the record is written whole on the first failure and only ever
 * updated in place. The header counts are the ones the repository's listing reported for the entry.
 */
@Data
public class CbomSyncSkipDto {

    @Schema(description = "UUID of the record", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Serial number (URN) of the CBOM the repository lists",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "Version of the CBOM the repository lists", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Whether the next sync runs still try the entry, or the retry budget is spent",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private CbomSyncSkipState state;

    @Schema(description = "Sync runs that tried the entry so far, the one that first failed included",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int attempts;

    @Schema(description = "When the entry first failed", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime firstSkippedAt;

    @Schema(description = "When the entry was last tried", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastAttemptAt;

    @Schema(description = "Why the last attempt failed, " + CbomDto.OPERATOR_WORDED_REASON,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(description = "Number of algorithms the repository listing reported for the entry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int algorithms;

    @Schema(description = "Number of certificates the repository listing reported for the entry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int certificates;

    @Schema(description = "Number of protocols the repository listing reported for the entry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int protocols;

    @Schema(description = "Number of crypto material items the repository listing reported for the entry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int cryptoMaterial;

    @Schema(description = "Total number of assets the repository listing reported for the entry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalAssets;
}

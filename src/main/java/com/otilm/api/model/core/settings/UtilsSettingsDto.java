package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * The utils section of the platform settings: the two auxiliary service URLs and the CBOM sync tunables.
 *
 * <p>
 * The tunables are flat {@code cbomSync*} siblings of the URLs, not a nested object: the prefix already namespaces the
 * group, and the names are published contract, so regrouping them would be a breaking rename.
 *
 * <p>
 * A {@code utils} update is stored as sent, the tunables included: a value left out returns to the platform default, as
 * a URL left out is cleared. An absent field and an explicit null arrive as the same {@code null}, so "left out means
 * unchanged" would leave no way back to the default through the API ({@code 0} is a real value).
 */
@Data
public class UtilsSettingsDto implements Serializable {

    /*
     * Compile-time constants: a consumer inlines them when it compiles, so a change here reaches core only when core
     * recompiles, while the @Max in the published jar changes at once. The contract is published; widen, never narrow.
     */
    /** A day: an overlap longer than that re-lists more history than any missed upload could plausibly need. */
    public static final int MAX_CBOM_SYNC_OVERLAP_SECONDS = 86_400;
    /** A document that could not be stored a thousand runs in a row is not going to become storable on its own. */
    public static final int MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS = 1_000;
    /** Every catch-up document is read and extracted; ten thousand per run is already a very large repository. */
    public static final int MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS = 10_000;
    /** A day: a written-off document removed on the day it lands was never an operator's backlog. */
    public static final int MIN_CBOM_SYNC_SKIP_RETENTION_DAYS = 1;
    /** Ten years: a written-off document kept longer than that is history, not an operator's backlog. */
    public static final int MAX_CBOM_SYNC_SKIP_RETENTION_DAYS = 3_650;
    /**
     * The CBOM Repository's own {@code limit} bounds, which this value is sent as: a page of none is not a page, and
     * the repository refuses more than a thousand. Not a platform choice -- the upstream contract's.
     */
    public static final int MIN_CBOM_SYNC_PAGE_SIZE = 1;
    public static final int MAX_CBOM_SYNC_PAGE_SIZE = 1_000;
    /** A batch of none never drains its work list. Ten thousand assets in one transaction is already a long lock. */
    public static final int MIN_CBOM_SYNC_ASSET_BATCH_SIZE = 1;
    public static final int MAX_CBOM_SYNC_ASSET_BATCH_SIZE = 10_000;
    /** A day: a document left alone longer than that is not being retried, it is being abandoned. */
    public static final int MAX_CBOM_SYNC_INGEST_RETRY_AFTER_SECONDS = 86_400;

    @URL
    @Schema(description = "URL of the Util Service", examples = {"http://util-service:8080"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String utilsServiceUrl;

    @URL
    @Schema(description = "URL of the CBOM Repository", examples = {"http://cbom-repository:8080"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cbomRepositoryUrl;

    @Min(value = 0, message = "cbomSyncOverlapSeconds must not be negative")
    @Max(value = MAX_CBOM_SYNC_OVERLAP_SECONDS, message = "cbomSyncOverlapSeconds must not exceed {value}")
    @Schema(description = "How far before the start of the last successful CBOM sync run each run re-lists the CBOM "
            + "Repository, in seconds. It should cover the clock skew between the platform and the repository's "
            + "object store plus the longest upload. 0 re-lists only from the start of the last run. Entries listed "
            + "twice are stored once", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0",
            maximum = "" + MAX_CBOM_SYNC_OVERLAP_SECONDS, defaultValue = "60")
    private Integer cbomSyncOverlapSeconds;

    @Min(value = 0, message = "cbomSyncSkippedRetryRuns must not be negative")
    @Max(value = MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS, message = "cbomSyncSkippedRetryRuns must not exceed {value}")
    @Schema(description = "How many later CBOM sync runs retry a repository entry that could not be stored before it "
            + "is given up on as permanently skipped; 0 gives an entry up at its first failure",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0",
            maximum = "" + MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS, defaultValue = "3")
    private Integer cbomSyncSkippedRetryRuns;

    @Min(value = 0, message = "cbomSyncMaxIngestDocuments must not be negative")
    @Max(value = MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS, message = "cbomSyncMaxIngestDocuments must not exceed {value}")
    @Schema(description = "How many CBOMs one sync run ingests cryptographic assets from, beyond the entries the run "
            + "has just stored: earlier failures, interrupted ingests and CBOMs uploaded through the platform API. 0 "
            + "turns that catch-up off", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0",
            maximum = "" + MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS, defaultValue = "50")
    private Integer cbomSyncMaxIngestDocuments;

    @Min(value = MIN_CBOM_SYNC_SKIP_RETENTION_DAYS, message = "cbomSyncSkipRetentionDays must be at least {value}")
    @Max(value = MAX_CBOM_SYNC_SKIP_RETENTION_DAYS, message = "cbomSyncSkipRetentionDays must not exceed {value}")
    @Schema(description = "How many days after its last attempt a CBOM Repository entry the sync gave up on "
            + "(permanently skipped) stays listed before its record is removed; an entry still being retried is kept "
            + "whatever its age. At least one day: a retention of nothing would remove a write-off as it lands",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "" + MIN_CBOM_SYNC_SKIP_RETENTION_DAYS,
            maximum = "" + MAX_CBOM_SYNC_SKIP_RETENTION_DAYS, defaultValue = "90")
    private Integer cbomSyncSkipRetentionDays;

    @Min(value = MIN_CBOM_SYNC_PAGE_SIZE, message = "cbomSyncPageSize must be at least {value}")
    @Max(value = MAX_CBOM_SYNC_PAGE_SIZE, message = "cbomSyncPageSize must not exceed {value}")
    @Schema(description = "How many entries one page of the CBOM Repository listing carries, sent as the search "
            + "`limit`. Pages are followed through the `Link rel=\"next\"` header, so this bounds one request rather "
            + "than one run", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "" + MIN_CBOM_SYNC_PAGE_SIZE,
            maximum = "" + MAX_CBOM_SYNC_PAGE_SIZE, defaultValue = "1000")
    private Integer cbomSyncPageSize;

    @Schema(description = "Whether a sync run ingests cryptographic assets at all. Off covers both discovery paths -- "
            + "the ingest of a document the run has just stored and the catch-up over the documents that still owe "
            + "one -- and leaves each CBOM exactly as it was found, so turning it back on resumes rather than repairs",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "true")
    private Boolean cbomSyncAssetIngestEnabled;

    @Min(value = MIN_CBOM_SYNC_ASSET_BATCH_SIZE, message = "cbomSyncAssetBatchSize must be at least {value}")
    @Max(value = MAX_CBOM_SYNC_ASSET_BATCH_SIZE, message = "cbomSyncAssetBatchSize must not exceed {value}")
    @Schema(description = "How many cryptographic assets one ingest transaction writes before committing, and the "
            + "page size the withdrawal of a CBOM's assets walks. It bounds how long one transaction holds its locks, "
            + "not how much work a run does", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            minimum = "" + MIN_CBOM_SYNC_ASSET_BATCH_SIZE, maximum = "" + MAX_CBOM_SYNC_ASSET_BATCH_SIZE,
            defaultValue = "100")
    private Integer cbomSyncAssetBatchSize;

    @Min(value = 0, message = "cbomSyncIngestRetryAfterSeconds must not be negative")
    @Max(value = MAX_CBOM_SYNC_INGEST_RETRY_AFTER_SECONDS,
            message = "cbomSyncIngestRetryAfterSeconds must not exceed {value}")
    @Schema(description = "How long a CBOM whose asset ingest is in progress or has failed is left alone before a run "
            + "offers it again, in seconds. Measured from the start of the attempt, so it has to exceed the longest "
            + "single document the deployment expects to ingest. 0 offers it to the very next run",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0",
            maximum = "" + MAX_CBOM_SYNC_INGEST_RETRY_AFTER_SECONDS, defaultValue = "1800")
    private Integer cbomSyncIngestRetryAfterSeconds;

}

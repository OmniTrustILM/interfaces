package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * The utils section of the platform settings: the two auxiliary service URLs and the CBOM sync policy, including how
 * long a document the sync gave up on stays listed.
 *
 * <p>
 * The policy values are flat {@code cbomSync*} siblings of the URLs, not a nested object. Settled in interfaces#974
 * (2026-09-16): the prefix already namespaces the group, and core#2250's retention value joins it the same way. Past
 * roughly five values a nested {@code cbomSync} object would start paying for itself -- but by then the regrouping is a
 * breaking rename, so it is weighed as a whole then, not per value.
 *
 * <p>
 * A {@code utils} update is stored as sent, the policy values included: a value left out returns to the platform
 * default, as a URL left out is cleared. Decided 2026-09-16 against "left out means unchanged": an absent field and an
 * explicit null are the same {@code null} here, so "unchanged" would leave no way back to the default through the API
 * ({@code 0} is a real value), and one rule per section beats two. The retention is the one value with a floor of one
 * rather than zero: it counts days after a write-off, and nothing kept for no time could be listed at all.
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
    /** Ten years: a written-off document kept longer than that is history, not an operator's backlog. */
    public static final int MAX_CBOM_SYNC_SKIP_RETENTION_DAYS = 3_650;

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

    @Min(value = 1, message = "cbomSyncSkipRetentionDays must be at least 1")
    @Max(value = MAX_CBOM_SYNC_SKIP_RETENTION_DAYS, message = "cbomSyncSkipRetentionDays must not exceed {value}")
    @Schema(description = "How many days after its last attempt a CBOM Repository entry the sync gave up on "
            + "(permanently skipped) stays listed before its record is removed; an entry still being retried is kept "
            + "whatever its age. At least one day: a retention of nothing would remove a write-off as it lands",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1",
            maximum = "" + MAX_CBOM_SYNC_SKIP_RETENTION_DAYS, defaultValue = "90")
    private Integer cbomSyncSkipRetentionDays;

}

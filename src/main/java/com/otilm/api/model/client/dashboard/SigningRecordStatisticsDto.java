package com.otilm.api.model.client.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

/**
 * Statistics for the Signing Records dashboard. Every figure is sourced from shared tables (never per-instance metrics)
 * and is security-filtered identically to the Signing Records list endpoint, so the numbers are cluster-wide consistent
 * and scoped to the signing profiles the caller may access.
 *
 * <p>
 * The figures come in two kinds. How much was signed in a period — {@code volumeOverTime}, {@code countLast24h} and
 * {@code countLast7d} — is historical: deleting a signing record, whether by hand, by retention or by
 * delete-after-retrieval, does not change it. Every other figure describes the records currently <em>retained</em> and
 * does follow those deletions. The two therefore disagree by design once records have been removed, and neither is a
 * count of every signing that ever happened: recording is opt-in per signing profile, and nothing is counted for a
 * profile version that has it switched off.
 */
@Data
@Schema(name = "SigningRecordStatisticsDto", description = "Signing Records dashboard statistics")
public class SigningRecordStatisticsDto {

    @Schema(description = "Total signing records currently retained (across the retention window)")
    private Long totalRetained;

    @Schema(description = "Signings performed in the last 24 hours, including those whose records have since been "
            + "deleted. History is kept per hour, so the window opens at the start of the hour rather than 24 "
            + "hours to the second.")
    private Long countLast24h;

    @Schema(description = "Signings performed in the last 7 days, including those whose records have since been "
            + "deleted. History is kept per hour, so the window opens at the start of the hour rather than 7 days "
            + "to the second.")
    private Long countLast7d;

    @Schema(description = "Number of distinct signing profiles that produced at least one retained record")
    private Long activeProfileCount;

    @Schema(description = "Number of distinct requesters that produced at least one retained record. "
            + "Lets the client render a \"+k more\" overflow beyond the top-N entries in statByRequester.")
    private Long distinctRequesterCount;

    @Schema(description = "Signing volume over the requested period, including signings whose records have since "
            + "been deleted. Ordered map of bucket start to count, keyed by ISO-8601 UTC instant (hourly buckets "
            + "for the 24h period, daily otherwise). Buckets are dense: every bucket in the window is present, "
            + "with 0 for buckets that had no signings.")
    private Map<String, Long> volumeOverTime;

    @Schema(description = "Retained record count by signing profile name")
    private Map<String, Long> statByProfile;

    @Schema(description = "Retained record count by requester username, limited to the top-N requesters by count. "
            + "Use distinctRequesterCount for the total. Records with no requester are grouped under \"Unassigned\".")
    private Map<String, Long> statByRequester;

    @Schema(description = "Retained record count by signing workflow type. Keys are SigningWorkflowType codes.")
    private Map<String, Long> statByWorkflowType;

    @Schema(description = "Retained record count by signing protocol. Keys are SigningProtocol codes, "
            + "grouped on the protocol persisted on each record at signing time.")
    private Map<String, Long> statByProtocol;

    @Schema(description = "Retained record count by signing scheme, flattened as the cross-product of signing scheme "
            + "and managed signing type: 'delegated', 'managed_static_key', 'managed_one_time_key' (and more later).")
    private Map<String, Long> statByScheme;
}

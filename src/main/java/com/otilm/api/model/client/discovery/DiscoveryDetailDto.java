package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryProgressDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import com.otilm.api.model.core.workflows.TriggerDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiscoveryDetailDto extends NameAndUuidDto {

    @Schema(description = "Discovery Kind. Absent for a run against a v2 Discovery Provider, which has no kinds.",
            examples = {"IP-HostName"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryStatus status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryStatus connectorStatus;

    @Schema(description = "Failure/Success Messages", examples = {"Failed due to network connectivity issues"})
    private String message;

    @Schema(description = "Date and time when Discovery started", nullable = true)
    private OffsetDateTime startTime;

    @Schema(description = "Date and time when Discovery finished", nullable = true)
    private OffsetDateTime endTime;

    @Schema(description = "How many certificates this discovery found and saved.", defaultValue = "0")
    private Integer totalCertificatesDiscovered;

    @Schema(description = "How many certificate items the Discovery Provider reported. Counts items, so a certificate "
            + "found on several hosts counts once per host; totalCertificatesDiscovered counts it once.",
            defaultValue = "0")
    private Integer connectorTotalCertificatesDiscovered;

    @Schema(description = "UUID of the Discovery Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Name of the Discovery Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "The connector interface this run is driven through, and so which generation drives it. "
            + "Absent for a run against a legacy v1 connector, which declares no connector interface.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConnectorInterfaceDto connectorInterface;

    @Schema(description = "List of Discovery Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Metadata of the Discovery")
    private List<MetadataResponseDto> metadata;

    @Schema(description = "List of associated triggers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerDto> triggers = new ArrayList<>();

    // The fields below are the discovery v2 additions. The object-typed ones carry
    // @JsonInclude(NON_NULL) individually so a v1 run's payload keeps its existing shape; a class-level
    // annotation would have changed every v1 response. runMessageCount is a primitive and always emitted,
    // 0 for a v1 run. resources and stoppable are always present too, synthesized by Core for v1 runs.

    // The arraySchema indirection is deliberate: a bare @Schema description on a collection member lands
    // on the items schema — a $ref to a shared component — and swagger-core hoists it onto that component
    // for every other API referencing it. arraySchema puts the text on the array.
    @ArraySchema(arraySchema = @Schema(description = "Resource types this run targets, as resource wire codes (e.g. "
            + "\"certificates\", \"keys\"). Always present: a run against a v1 connector reports "
            + "[\"certificates\"], synthesized by Core, so a client never inspects the connector "
            + "to learn what a run was after.", requiredMode = Schema.RequiredMode.REQUIRED))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Resource> resources;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "Progress counters reported by the connector, with an optional per-resource breakdown. "
            + "Omitted for a v1 run and when the connector reports no progress. The counters inside are "
            + "independently optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DiscoveryProgressDto progress;

    /**
     * A count rather than the log itself. The messages are their own paged resource
     * ({@code GET /v1/discoveries/{uuid}/messages}), because a client polls this detail while a run is live and a log
     * bounded only by "large" would ride along on every poll. The count is enough to badge the log without reading it,
     * and it is what tells a client whether the log is worth opening at all.
     *
     * <p>
     * <b>Relation to {@code message}:</b> {@code message} carries the single summary reason for the run's current
     * status; the messages this counts accumulate over the run's lifetime instead.
     *
     * <p>
     * Primitive and REQUIRED: a run with nothing to report has none, which is 0 rather than absent.
     */
    @Schema(description = "How many distinct advisory messages this run collected, as counted by the run messages "
            + "listing. Repeated problems are aggregated, so this counts kinds of problem rather than occurrences. "
            + "A non-zero count does not imply the run failed. Always 0 for runs against a v1 Discovery Provider.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long runMessageCount;

    /** The drain cursor: the highest item sequence received from the Provider. */
    @Schema(description = "How many items this discovery has received from the Discovery Provider, across all "
            + "resource types. Usually the same as the number the items listing returns, and larger when an item "
            + "could not be kept — a malformed payload, or one already collected earlier in the run. Whatever the "
            + "provider has produced but not yet handed over shows in progress, not here. Not set for a discovery "
            + "run against a v1 Discovery Provider.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long itemsDiscovered;

    @Schema(description = "How many of the items collected were not already in the inventory when this run staged "
            + "them. Counted per item rather than per object, so the same certificate found on two hosts counts "
            + "twice.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsNewlyDiscovered;

    /**
     * Counted from the platform's own rows on every read, in one query with {@code itemsFailed} and
     * {@code itemsNewlyDiscovered}, so the three never disagree.
     */
    @Schema(description = "How many of the newly discovered items are now in the inventory. Counts only items that "
            + "were imported cleanly; one the platform could not import is counted by itemsFailed instead. "
            + "itemsNewlyDiscovered less this and itemsFailed is what the run has still to import.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsProcessed;

    @Schema(description = "How many of the newly discovered items the platform could not import, each for a reason "
            + "recorded against the item in the items listing. A discovery can finish with a non-zero count here — "
            + "it ends with a warning rather than a failure.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsFailed;

    /**
     * <b>Provenance:</b> declared by the connector at initiate and refreshed on resume; derived by Core from the
     * interface-level {@code discoveryStopResume} flag when the connector left it undeclared.
     *
     * <p>
     * <b>Presence:</b> synthesized as {@code false} for a run against a v1 connector, which cannot stop.
     */
    @Schema(description = "Whether this run can be stopped and later resumed. Always present; false for "
            + "runs against v1 connectors. This flag says whether the run has the capability; the run "
            + "status decides which control is currently valid (stop while in progress, resume while "
            + "stopped). The connector may still refuse a stop at runtime past the point of no return.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean stoppable;
}

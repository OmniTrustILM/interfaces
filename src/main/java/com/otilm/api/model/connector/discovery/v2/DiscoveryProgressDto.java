package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Run-level progress: how far through its own work the connector is, plus what that work has yielded per resource.
 * Carried by the {@code progress} field of {@link DiscoveryStatusResponseDto} (polled) and, through the
 * {@code type}-carrying subclass {@link com.otilm.api.model.connector.discovery.v2.event.DiscoveryProgressEvent}, by
 * the flat {@code progress} stream event (pushed).
 *
 * <p>
 * Work and yield are separate quantities: only work gives a completion ratio, and yield is well defined only per
 * resource. The run's total yield is {@code highestSequence}.
 *
 * <p>
 * Nesting stops here: {@code byResource} holds the leaf type, never this one. A self-referential progress type makes
 * swagger-core truncate the component, dropping {@code byResource}, when the graph is entered through
 * {@link DiscoveryEvent}.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
// The description lives here, on the component itself, so it reads the same from every endpoint that references
// it — a description on any referencing field would be hoisted over it (see the field comments below and
// progressComponentsAreIdenticalFromEveryEntryPoint).
@Schema(description = "How far a discovery has got: how much of the provider's work is done, and optionally how "
        + "many items it has found per resource type.")
public class DiscoveryProgressDto {

    @Schema(description = "How many targets the provider has attempted so far, including ones that failed. A "
            + "target is one unit of the provider's work — one address and port, one inventory entry, one keystore "
            + "alias — and can yield many items or none. Omitted if the provider cannot count its work.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsProcessed;

    @Schema(description = "How many targets this discovery will attempt in total — exact if the provider knows, "
            + "an estimate otherwise, and it may change as the run goes on. Use it with targetsProcessed to show "
            + "progress. Omitted when the provider cannot know the total; there is then no percentage to show, and "
            + "clients must not guess one.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsTotal;

    @Schema(description = "How many targets could not be examined — an unreachable host, a refused connection, "
            + "a target that answered nothing usable. Included in targetsProcessed, not added to it, so a mostly "
            + "dark range still reaches its total. Failures here do not make the discovery itself fail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsFailed;

    @Schema(description = "Free-text label for what the provider is doing (e.g. \"scanning\", "
            + "\"enumerating\"); omitted if it has none", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;

    // A plain @Schema, unlike the $ref-valued fields elsewhere in this contract: a Map's $ref sits under
    // additionalProperties rather than beside the description, so there is nothing for swagger-core to hoist
    // onto the shared component. ALL_OF_REF would be wrong here -- on a Map field it drops the description.
    @Schema(description = "How many items were found, broken down by resource type. Present only if the "
            + "provider reports the breakdown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            propertyNames = Resource.class)
    private Map<Resource, DiscoveryResourceProgressDto> byResource;

    /**
     * Stamped by the platform on the way in; inside this object so one write replaces the counters and their date
     * together.
     */
    @Schema(description = "When the platform recorded this report. A discovery is polled on a backoff that settles "
            + "at several minutes, so counters can be older than the response carrying them; this says how much "
            + "older. Set by the platform — a value sent by a Discovery Provider is ignored.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY,
            examples = {"2026-09-13T14:32:07.000+02:00"})
    private OffsetDateTime updatedAt;
}

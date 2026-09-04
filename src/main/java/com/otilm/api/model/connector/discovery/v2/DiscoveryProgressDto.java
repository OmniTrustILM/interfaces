package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * <b>Work and yield are counted separately because they are different quantities.</b> Work is what the connector
 * attempts and is the only thing that gives a completion ratio; a sweep knows its target count at initiate but cannot
 * know how many certificates an address range holds until it has swept it. Yield is items, which are only well defined
 * per resource — one keystore alias can produce a certificate and a key, so a single run-wide item count would have no
 * agreed meaning. The run's total yield is {@code highestSequence}, which is exact and required on every status and
 * drain response.
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
@Schema(description = "Run-level progress of a discovery run: how much of its work the connector has done, and "
        + "an optional breakdown of what that work has yielded per resource.")
public class DiscoveryProgressDto {

    @Schema(description = "Number of targets the connector has attempted so far, failures included. A target is one "
            + "unit of the connector's own work — an address-and-port probe for a network sweep, an inventory "
            + "entry, a keystore alias, a log record — and one target can yield many items or none. Omitted "
            + "when the connector cannot count its work.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsProcessed;

    @Schema(description = "Total number of targets this run will attempt: exact where the connector knows it, "
            + "estimated otherwise. The denominator of targetsProcessed, so the two together are the run's "
            + "completion ratio, and it may be revised mid-run as later phases refine it. Omitted when the work "
            + "is unbounded — an endless stream, an enumeration whose size is unknown until it ends — in which "
            + "case the run has no completion ratio and a consumer must not invent one.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsTotal;

    @Schema(description = "Number of targets attempted and not examinable — an unreachable host, a refused "
            + "connection, a target that answered nothing usable. Counted within targetsProcessed rather than "
            + "beside it: a failed target was still attempted, so a run whose range was mostly dark still "
            + "reaches its total. Reporting failures here does not degrade the run — a connector whose result "
            + "is not worth trusting reports the run itself as failed.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsFailed;

    @Schema(description = "Connector-defined free-text phase label (e.g. \"scanning\", \"enumerating\"); "
            + "omitted when the connector has no phase concept", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;

    // A plain @Schema, unlike the $ref-valued fields elsewhere in this contract: a Map's $ref sits under
    // additionalProperties rather than beside the description, so there is nothing for swagger-core to hoist
    // onto the shared component. ALL_OF_REF would be wrong here -- on a Map field it drops the description.
    @Schema(description = "Item yield per resource, keyed by resource code. The counters inside are counted in "
            + "items, never in targets. Present only when the connector attributes its yield by resource.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, propertyNames = Resource.class)
    private Map<Resource, DiscoveryResourceProgressDto> byResource;
}

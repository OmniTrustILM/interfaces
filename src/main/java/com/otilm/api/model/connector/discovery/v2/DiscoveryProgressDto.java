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
 * Run-level progress: how much of its work the connector has done, and what it has found per resource. Only work gives
 * a completion ratio; the run's total yield is {@code highestSequence}.
 *
 * <p>
 * Every processed target yielded items, was empty or failed, never two of these; the first is the remainder and is not
 * sent. The outcomes are separate fields rather than an enum-keyed map: a reader skips an unknown field but fails the
 * whole object on an unknown enum key, so an older platform would refuse every answer reporting a newer outcome.
 *
 * <p>
 * {@code byResource} holds the leaf type, never this one: a self-referential type makes swagger-core truncate the
 * component on some entry paths.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
// Described on the component, so it reads the same from every endpoint that references it.
@Schema(description = "How far a discovery has got: the provider's work done so far, and optionally the items found "
        + "per resource type.")
public class DiscoveryProgressDto {

    @Schema(description = "Targets attempted so far, whatever their outcome. A target is a unit of work a person "
            + "would recognise, such as an address and port, a CA, a CA and template, a mount or a report. Omitted if "
            + "the provider cannot count its work. A provider whose unit would only be a page of search results omits "
            + "all target counters and reports byResource instead.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsProcessed;

    @Schema(description = "Targets the discovery will attempt in total: exact, or an estimate that may change. Omitted "
            + "when unknown, and a client must then not guess a percentage.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsTotal;

    @Schema(description = "Targets the provider expected to read and could not, such as access refused, the source "
            + "unreachable or an unreadable answer. Part of targetsProcessed, never also counted as empty. The "
            + "discovery does not fail for them, but ends with a warning.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsFailed;

    @Schema(description = "Targets attempted that held nothing to discover, such as an address with nothing "
            + "listening or an empty mount, template or report. Part of targetsProcessed, never also counted as "
            + "failed: an empty target is a normal outcome. Omitted if the provider does not tell empty targets "
            + "apart.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long targetsEmpty;

    @Schema(description = "What the provider is doing, as free text (e.g. \"scanning\"). Omitted if it has none.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phase;

    // Plain @Schema: a Map's $ref sits under additionalProperties, so nothing is hoisted, and ALL_OF_REF would drop
    // the description.
    @Schema(description = "Items found per resource type. Present only if the provider reports the breakdown.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, propertyNames = Resource.class)
    private Map<Resource, DiscoveryResourceProgressDto> byResource;

    /** Inside this object, so one write replaces the counters and their date together. */
    @Schema(description = "When the platform recorded this report. Polling backs off to several minutes, so the "
            + "counters can be older than the response carrying them. Set by the platform; a value sent by the "
            + "provider is ignored.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY, examples = {"2026-09-13T14:32:07.000+02:00"})
    private OffsetDateTime updatedAt;
}

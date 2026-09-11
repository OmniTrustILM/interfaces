package com.otilm.api.model.core.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * One item a Discovery run staged, as Core holds it — the resource-agnostic counterpart to
 * {@link DiscoveryCertificateDto}.
 *
 * <p>
 * Core-facing on purpose, rather than the connector's own {@code DiscoveredItemDto}: what a connector reported and what
 * Core did with it are different facts, and the second — processing outcome, inventory linkage, novelty — is not a
 * connector's to know. {@code payload} deliberately reuses the connector-defined union, because the resource-specific
 * data a connector reports is genuinely shared, and re-declaring it Core-side would be duplication that could drift.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DiscoveryItemDto {

    @Schema(description = "UUID of the staged Discovery item", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "UUID of this item's object in inventory. For a certificate, present as soon as one "
            + "with the same content exists — including from an earlier run, so it may be present while "
            + "processed is false. For every other resource it is what the item became: absent until "
            + "processed, and absent permanently if processing failed.")
    private String inventoryUuid;

    // Primitive, unlike the connector's DiscoveredItemDto.sequence: that one is inbound, where a boxed Long lets a
    // value the connector omitted arrive as null and be rejected rather than silently becoming 0. This one is
    // outbound and always populated — the connector's run-wide cursor for v2-staged items, synthesized by Core for
    // items a v1 provider staged — so a box would only invent an absent state the contract says cannot occur.
    @Schema(description = "Position of this item in the run. For runs against a v2 Discovery Provider this is the "
            + "provider's run-wide sequence; for runs against a v1 provider, which never numbered its items, Core "
            + "synthesizes it from staging order.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long sequence;

    @Schema(description = "Reference identifying this item within the run, unique per resource type. "
            + "Provider-assigned for v2-staged items; for certificates staged by a v1 provider Core uses the "
            + "certificate fingerprint, which is the identity it deduplicates on.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String uniqueRef;

    @Schema(description = "When the Discovery Provider reported discovering this item")
    private OffsetDateTime discoveredAt;

    @Schema(description = "Resource-specific data the Discovery Provider reported, discriminated by resource",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveredItemPayloadDto payload;

    @Schema(description = "True when the object was not already in the inventory at the time this run staged it, "
            + "false when the run rediscovered something the inventory already held.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean newlyDiscovered;

    @Schema(description = "Indicator whether processing of the staged item has been attempted; processedError and "
            + "inventoryUuid convey the outcome.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean processed;

    @Schema(description = "Reason recorded against this item, if any — not only a failure, and not tied to "
            + "processed. An item that was handled can carry one, and an item that was never attempted carries one "
            + "while processed stays false.")
    private String processedError;

    @Schema(description = "Item metadata as reported by the Discovery Provider — where the item was found "
            + "(e.g. IP address and port). Absent when the provider reported none.")
    private List<MetadataAttribute> meta;

    /**
     * Derived from the payload rather than stored, so the two can never disagree — the same reasoning as the
     * connector's {@code DiscoveredItemDto}, which hides its derived accessor with {@code @JsonIgnore}. This one stays
     * on the wire deliberately: it is the value the {@code resource} query filter matches on, and clients group by it
     * without reaching into the payload union.
     */
    // READ_ONLY, not just getter-only: a bare getter serializes but is unknown to deserialization, so a strict
    // mapper (FAIL_ON_UNKNOWN_PROPERTIES) would reject this DTO's own serialized form. READ_ONLY registers the
    // property and ignores it on input, so the derived value always wins.
    // No @Schema description on purpose: Resource is a platform-wide schema component, and OpenAPI 3.0 cannot
    // carry a description beside a $ref — swagger-core would hoist the text onto the shared component
    // (discoveryDoesNotRewriteThePlatformWideResourceComponent pins this). The Javadoc above explains the property.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public Resource getResource() {
        return payload != null ? payload.getResource() : null;
    }
}

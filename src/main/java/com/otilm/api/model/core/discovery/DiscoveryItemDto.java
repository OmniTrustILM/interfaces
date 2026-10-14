package com.otilm.api.model.core.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.NameAndUuidDto;
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
 * One item a discovery run staged, as Core holds it; the resource-agnostic counterpart to
 * {@link DiscoveryCertificateDto}. It reuses the connector's payload union and adds what only Core knows: the
 * processing outcome, the inventory link and whether the item was new.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DiscoveryItemDto {

    @Schema(description = "UUID of the staged item", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "The inventory object the item became, of the item's resource. A certificate links as soon "
            + "as one with the same content exists, even before processing; other resources link once processed, and "
            + "never if processing failed.", schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    private NameAndUuidDto inventory;

    // Primitive, unlike the inbound DiscoveredItemDto.sequence: always populated, so a box would invent an absent
    // state.
    @Schema(description = "Position of the item in the run: the provider's sequence in a v2 run, the staging order in "
            + "a v1 run.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long sequence;

    @Schema(description = "Identifies the item within the run, unique per resource type: assigned by the provider in "
            + "a v2 run, the certificate fingerprint in a v1 run.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uniqueRef;

    @Schema(description = "When the Discovery Provider reported discovering this item")
    private OffsetDateTime discoveredAt;

    /** Stored, not derived from {@link #payload}, so an item whose payload cannot be decoded still names it. */
    // No description: it would be hoisted onto the shared Resource component.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "What the Discovery Provider reported for the item, typed by its resource. Absent when the "
            + "stored payload can no longer be decoded; the item is still listed.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DiscoveredItemPayloadDto payload;

    @Schema(description = "Whether the object was new to the inventory when this run staged it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean newlyDiscovered;

    @Schema(description = "Whether processing of the item has been attempted; processedError and inventory give the "
            + "outcome.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean processed;

    @Schema(description = "A reason recorded against the item, not necessarily a failure. A processed item can carry "
            + "one, and so can one never attempted.")
    private String processedError;

    @Schema(description = "Metadata the Discovery Provider reported, such as where the item was found. Absent when "
            + "there is none.")
    private List<MetadataAttribute> meta;

}

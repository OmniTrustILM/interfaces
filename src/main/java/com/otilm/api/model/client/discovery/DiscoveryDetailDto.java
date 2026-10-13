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

    @Schema(description = "Discovery kind. Absent for a v2 run, which has none.", examples = {"IP-HostName"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryStatus status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryStatus connectorStatus;

    @Schema(description = "Why the discovery is in its current status",
            examples = {"Failed due to network connectivity issues"})
    private String message;

    @Schema(description = "When the discovery started", nullable = true)
    private OffsetDateTime startTime;

    @Schema(description = "When the discovery finished", nullable = true)
    private OffsetDateTime endTime;

    @Schema(description = "Certificates this discovery found and saved", defaultValue = "0")
    private Integer totalCertificatesDiscovered;

    @Schema(description = "Certificate items the Discovery Provider reported: a certificate found on several hosts "
            + "counts once per host, unlike in totalCertificatesDiscovered.", defaultValue = "0")
    private Integer connectorTotalCertificatesDiscovered;

    @Schema(description = "UUID of the Discovery Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Name of the Discovery Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "The connector interface the run is driven through. Absent for a v1 run, and for an ended run "
            + "whose connector was deleted.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
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

    // Discovery v2 fields. The object-typed ones are NON_NULL one by one, so a v1 run's payload keeps its shape.
    // arraySchema puts a collection's description on the array, not on the shared item component.
    @ArraySchema(
            arraySchema = @Schema(
                    description = "Resource types the run targets, as resource codes. Always "
                            + "present; a v1 run reports [\"certificates\"].",
                    requiredMode = Schema.RequiredMode.REQUIRED))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Resource> resources;

    // ALL_OF_REF keeps this description off the shared component; see ConnectorInterfaceDto.
    @Schema(description = "Progress the Discovery Provider last reported. Omitted for a v1 run and until the "
            + "provider reports any.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            schemaResolution = Schema.SchemaResolution.ALL_OF_REF)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DiscoveryProgressDto progress;

    /** A count, not the log: the messages are paged separately, since this detail is polled while a run is live. */
    @Schema(description = "Number of entries in the run messages listing. Repeats are aggregated, so it counts kinds "
            + "of problem, and a non-zero count does not mean the run failed. For a v1 run it is 0 unless the run's "
            + "metadata could not be stored.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long runMessageCount;

    /** The drain cursor: the highest item sequence received from the Provider. */
    @Schema(description = "Items received from the Discovery Provider, across all resource types. Usually what the "
            + "items listing returns; larger when an item could not be kept, being malformed or already collected. "
            + "Absent for a v1 run.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long itemsDiscovered;

    @Schema(description = "Items that were new to the inventory when staged. Counted per item, so a certificate found "
            + "on two hosts counts twice.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsNewlyDiscovered;

    /**
     * Counted from the platform's own rows on every read, in one query with {@code itemsFailed} and
     * {@code itemsNewlyDiscovered}, so the three never disagree.
     */
    @Schema(description = "Newly discovered items imported cleanly. itemsNewlyDiscovered minus this and itemsFailed "
            + "is what is still to import.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsProcessed;

    @Schema(description = "Newly discovered items the platform could not import, each with its reason in the items "
            + "listing. A discovery with failed items ends with a warning, not a failure.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long itemsFailed;

    /** Declared by the connector at initiate and resume; from the {@code discoveryStopResume} flag otherwise. */
    @Schema(description = "Whether the run can be stopped and resumed; false for a v1 run. The status decides which "
            + "applies now: stop while in progress, resume while stopped. The connector can still refuse a stop past "
            + "the point of no return.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean stoppable;
}

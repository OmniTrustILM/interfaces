package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Base of every discovery v2 request body. The connector is stateless, so identity, configuration and the checkpoint
 * are replayed on every lifecycle call.
 */
@Getter
@Setter
@ToString
public abstract class DiscoveryV2ScopedRequestDto {

    @Schema(description = "Discovery run identifier assigned by Core when the run was created",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "runId is required")
    private UUID runId;

    // @ArraySchema puts the description on the array, and ALL_OF_REF keeps it off the shared Resource component,
    // which these bodies also reach through the attribute graph.
    @ArraySchema(arraySchema = @Schema(
            description = "Resource types this run covers, fixed at initiate and replayed unchanged on every later "
                    + "call. Not derivable from resourceAttributes, which omits a resource that declares no "
                    + "attributes of its own.",
            requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = Resource.class, schemaResolution = Schema.SchemaResolution.ALL_OF_REF))
    @NotEmpty(message = "resources is required (must contain at least one resource type)")
    private List<@NotNull(message = "resources must not contain a null resource type") Resource> resources;

    // Excluded from toString: the attribute lists can carry target credentials and the checkpoint is opaque. runId
    // and resources stay in, since log lines correlate by them.
    @Schema(description = "Opaque run handle from the initiate, stop or resume response that last carried one, "
            + "replayed so the stateless connector can resolve its run state. Absent on initiate, which mints it. "
            + "Serialized size is capped at 64 KB.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;

    // Optional, unlike the authority v3 request lists: a connector may define no run-level attributes at all.
    @Schema(description = "Run-level attributes supplied when the run was initiated; optional, since a connector "
            + "may define none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<RequestAttribute> attributes;

    @Schema(description = "Per-resource attributes keyed by resource code (e.g. \"certificates\", \"keys\"); "
            + "optional, since a connector may define none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            propertyNames = Resource.class)
    @ToString.Exclude
    private Map<Resource, List<RequestAttribute>> resourceAttributes;
}

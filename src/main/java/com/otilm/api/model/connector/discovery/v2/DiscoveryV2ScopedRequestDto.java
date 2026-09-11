package com.otilm.api.model.connector.discovery.v2;

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
 * Abstract base for all discovery v2 request bodies. The discovery connector is stateless and keeps no Core-visible
 * state, so identity ({@code runId}) and configuration (the resource set and the attribute lists) are replayed on every
 * lifecycle call.
 */
@Getter
@Setter
@ToString
public abstract class DiscoveryV2ScopedRequestDto {

    @Schema(description = "Discovery run identifier assigned by Core when the run was created",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "runId is required")
    private UUID runId;

    // Keeps this prose off the shared, platform-wide Resource component. @ArraySchema puts it on the array, since a
    // bare @Schema on a collection lands on the item. ALL_OF_REF is needed too: these bodies also reach Resource
    // through the attribute graph below, and on that second visit swagger-core writes the description onto it.
    @ArraySchema(arraySchema = @Schema(
            description = "Resource types this run covers, fixed when it was initiated and replayed unchanged on "
                    + "every later call. Not derivable from resourceAttributes, which omits any resource that "
                    + "declares no attributes of its own.",
            requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = Resource.class, schemaResolution = Schema.SchemaResolution.ALL_OF_REF))
    @NotEmpty(message = "resources is required (must contain at least one resource type)")
    private List<@NotNull(message = "resources must not contain a null resource type") Resource> resources;

    // Excluded from toString: attributes/resourceAttributes below can carry target credentials,
    // and meta is an opaque connector-defined handle with no logging value of its own. runId and
    // resources are deliberately left in: they identify and scope the run, which log lines correlate by.
    @Schema(description = "Connector-defined metadata returned in the original discovery initiate/stop/resume "
            + "response, replayed here so the stateless connector can resolve its run state; absent on "
            + "the initiate call itself, which is the call that mints it. Serialized size is capped at " + "64 KB.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<MetadataAttribute> meta;

    // Deliberately optional, unlike AuthorityV3ScopedRequestDto's REQUIRED attribute lists (which
    // use @NotNull(message = "... may be empty list, but must be present")): a discovery connector
    // that defines no run-level attributes at all is legitimate (for example, one whose targets are
    // hardcoded or discovered without configuration), so there is no "empty list, but must be
    // present" value to require here.
    @Schema(description = "Run-level attributes supplied when the discovery run was initiated; optional, "
            + "since a connector may define no run-level attributes at all.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<RequestAttribute> attributes;

    @Schema(description = "Per-resource attributes, keyed by resource code (e.g. \"certificates\", \"keys\"); "
            + "optional, since a connector may define none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            propertyNames = Resource.class)
    @ToString.Exclude
    private Map<Resource, List<RequestAttribute>> resourceAttributes;
}

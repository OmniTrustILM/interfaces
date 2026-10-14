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

    @Schema(description = "Run identifier, assigned by Core", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "runId is required")
    private UUID runId;

    // @ArraySchema puts the description on the array; ALL_OF_REF keeps it off the shared Resource component.
    @ArraySchema(arraySchema = @Schema(
            description = "Resource types the run covers, fixed at initiate and replayed unchanged. Not derivable "
                    + "from resourceAttributes, which leaves out a resource with no attributes of its own.",
            requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = Resource.class, schemaResolution = Schema.SchemaResolution.ALL_OF_REF))
    @NotEmpty(message = "resources is required (must contain at least one resource type)")
    private List<@NotNull(message = "resources must not contain a null resource type") Resource> resources;

    // Excluded from toString: the attribute lists can carry target credentials and the checkpoint is opaque.
    @Schema(description = "Opaque run handle from the last initiate, stop or resume response that carried one. "
            + "Absent on initiate, which mints it. At most 64 KB serialized.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString.Exclude
    private List<MetadataAttribute> checkpoint;

    @Schema(description = "Run-level attributes supplied at initiate. Optional: absent or empty when the connector "
            + "defines none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private List<RequestAttribute> attributes;

    @Schema(description = "Per-resource attributes, keyed by resource code. Optional: absent or empty when the "
            + "connector defines none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            propertyNames = Resource.class)
    @ToString.Exclude
    private Map<Resource, List<RequestAttribute>> resourceAttributes;
}

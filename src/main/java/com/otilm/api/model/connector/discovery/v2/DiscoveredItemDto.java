package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A single item produced by a discovery run: one certificate or one cryptographic key found by the connector.
 *
 * <p>
 * {@code resource} is not a field of this container: it is {@code payload}'s own discriminator (see
 * {@link DiscoveredItemPayloadDto}). {@link #getResource()} is a derived, read-only, {@code @JsonIgnore}d convenience
 * that unwraps it.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscoveredItemDto {

    @Schema(description = "Dense per-run item sequence (1, 2, 3, ... with no holes); the drain/stream cursor "
            + "value after which the next batch starts", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "sequence is required")
    @Min(value = 1, message = "sequence must be positive")
    private Long sequence;

    @Schema(description = "Connector-side natural key that Core uses to dedupe this item across drains and retries",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "uniqueRef is required")
    private String uniqueRef;

    @Schema(description = "Resource-specific payload; its concrete type is selected by resource",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "payload is required")
    @Valid
    private DiscoveredItemPayloadDto payload;

    // Deliberately not @Valid: MetadataAttribute carries no bean-validation constraints of its
    // own, and no other List<MetadataAttribute> meta field in this codebase cascades either
    // (e.g. CertificateSignRequestDtoV3#meta) — payload is the only nested DTO here that needs it.
    @Schema(description = "Item metadata (platform convention). Where an item was found (e.g. IP and port, "
            + "file path, HSM slot) belongs here as typed, labeled entries, never in payload.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "Timestamp at which the connector discovered this item",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime discoveredAt;

    /**
     * Derived from {@code payload}, null-safe, and excluded from the wire (see class javadoc). Not a field: there is
     * nothing to set independently of {@code payload}, and a setter would invite the two to disagree.
     */
    @JsonIgnore
    public Resource getResource() {
        return payload != null ? payload.getResource() : null;
    }
}

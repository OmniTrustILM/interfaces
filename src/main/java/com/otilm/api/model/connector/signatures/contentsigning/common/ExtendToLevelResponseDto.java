package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Result of {@code extendToLevel}: the extended document and an account of what was fetched to build it.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExtendToLevelResponse",
        description = "The extended document and the manifest of artifacts fetched to produce it. Populated on a "
                + "synchronous 200; on an asynchronous 202 the document and manifest are absent and "
                + "extendOperationMeta carries the tracking handle instead.")
public class ExtendToLevelResponseDto {

    @ToString.Exclude
    @Null(message = "extendedDocument must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotNull(message = "extendedDocument is required for synchronous execution", groups = SynchronousResponse.class)
    @Schema(description = "The signed document with its validation material embedded, base64-encoded in JSON. "
            + "Populated on a synchronous 200; null on an asynchronous 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private byte[] extendedDocument;

    @Null(message = "fetchManifest must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotNull(message = "fetchManifest may be an empty list, but must be present for synchronous execution",
            groups = SynchronousResponse.class)
    @Schema(description = "Every artifact the connector fetched to build the material it embedded. May be an empty "
            + "list when the supplied prefetched material already covered the signature, but must be present on a "
            + "synchronous 200 — an absent manifest would read as \"the connector did not look\", which is a "
            + "different claim from \"the connector found nothing to fetch\".",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull(
            message = "fetchManifest must not contain null items") @Valid FetchedArtifactDto> fetchManifest;

    @Null(message = "extendOperationMeta must be absent for synchronous execution", groups = SynchronousResponse.class)
    @NotEmpty(message = "extendOperationMeta must contain at least one item for asynchronous execution",
            groups = AsynchronousResponse.class)
    @Schema(description = "Connector-defined metadata tracking the operation. Present on an asynchronous 202; supply "
            + "it to /extendToLevel/status and /extendToLevel/cancel.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull(
            message = "extendOperationMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> extendOperationMeta;
}

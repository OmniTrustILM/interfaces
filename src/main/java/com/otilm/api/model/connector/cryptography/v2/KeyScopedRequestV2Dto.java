package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Context for V2 cryptography request bodies that operate on an existing key.
 * The inherited token context and {@code keyMeta} identify the target key.
 */
@Getter
@Setter
public class KeyScopedRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    @Schema(description = "Connector-defined metadata identifying the key, as returned when the key was created, "
            + "listed or identified. Supply the metadata unchanged in subsequent requests for the key.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyMeta is required and must not be empty")
    private List<@NotNull @Valid MetadataAttributeV2> keyMeta;
}

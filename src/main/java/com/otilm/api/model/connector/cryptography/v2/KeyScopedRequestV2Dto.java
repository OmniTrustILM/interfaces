package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Context for V2 cryptography request bodies that operate on an existing key. The inherited token context and
 * {@code keyMeta} identify the target key.
 */
@Getter
@Setter
@Schema(name = "KeyScopedRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class KeyScopedRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    @Schema(description = "Connector-defined metadata identifying the key, as returned when the key was created, "
            + "listed or identified. Supply the metadata unchanged in subsequent requests for the key. Metadata must "
            + "identify the key durably—it must remain valid across connector restarts and sessions; ephemeral handles "
            + "must not be used.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyMeta is required and must not be empty")
    private List<@NotNull @ValidMetadataAttribute MetadataAttribute> keyMeta;
}

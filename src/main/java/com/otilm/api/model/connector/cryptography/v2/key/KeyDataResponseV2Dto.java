package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response envelope for asynchronous key destruction.
 * {@code operationMeta} contains the tracking handle and {@code keyData} is absent.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "KeyDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyDataResponseV2Dto {

    @Schema(description = "Connector-defined operation tracking handle returned on async 202 and replayed on "
            + "/keys/destroy/status and /keys/destroy/cancel.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> operationMeta;

    @Valid
    @Schema(description = "Role-specific v2 key descriptor. Populated on sync 200; null on async 202. "
            + "Secret and private variants contain no key material; only the public variant may carry SPKI.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key response property: " + property);
    }
}

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
 * Response envelope specific to secret-key creation.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "SecretKeyDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class SecretKeyDataResponseV2Dto {

    @Schema(description = "Connector-defined key handle. Present on a synchronous 200 response.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> keyMeta;

    @Schema(description = "Connector-defined operation tracking handle. Present on an asynchronous 202 response.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> operationMeta;

    @Valid
    @Schema(description = "Created secret-key descriptor. Null on an asynchronous 202 response.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SecretKeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported secret-key response property: " + property);
    }
}

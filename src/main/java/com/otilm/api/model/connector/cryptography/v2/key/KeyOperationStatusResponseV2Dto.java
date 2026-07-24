package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response from the v2 key status endpoints. Reused across key creation and key destruction.
 * At most one of {@code keyData} / {@code keyPairData} is ever populated, and for destroy both are always null.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "KeyOperationStatusResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyOperationStatusResponseV2Dto {

    @Schema(description = "Operation status as known to the connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OperationStatus status;

    @Schema(description = "Created secret key. Populated when status=COMPLETED for /keys/secret. "
            + "Always null for /keys/pair and /keys/destroy status responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private SecretKeyDataResponseV2Dto keyData;

    @Schema(description = "Created key pair. Populated when status=COMPLETED for /keys/pair. "
            + "Always null for /keys/secret and /keys/destroy status responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private KeyPairDataResponseV2Dto keyPairData;

    @Schema(description = "Optional updated connector-defined metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> operationMeta;

    @Schema(description = "Failure detail when status=FAILED — curated message text (no raw exception messages)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key-operation status response property: " + property);
    }
}

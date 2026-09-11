package com.otilm.api.model.connector.cryptography.v2;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for polling or cancelling an asynchronous V2 cryptography operation.
 *
 * <p>
 * The tracking handle is sufficient by itself: callers do not resend token, token-profile, key-usage, or key context.
 * The connector must keep the handle valid for the operation's entire tracking lifetime, including after the operation
 * reaches a terminal state for as long as that state remains available through the status endpoint.
 * </p>
 */
@Getter
@Setter
@ToString
@Schema(name = "OperationTrackingRequestV2Dto", description = "Tracking handle for polling or cancelling an "
        + "asynchronous cryptography operation. The connector must keep the handle valid for the operation's entire "
        + "tracking lifetime.", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class OperationTrackingRequestV2Dto {

    @Schema(description = "Opaque connector-defined tracking handle returned in the original 202 Accepted response. "
            + "It is sufficient without token, token-profile, key-usage, or key metadata and must remain valid for "
            + "the operation's entire tracking lifetime.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "operationMeta is required and must not be empty")
    private List<@NotNull(
            message = "operationMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> operationMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 operation-tracking request property: " + property);
    }
}

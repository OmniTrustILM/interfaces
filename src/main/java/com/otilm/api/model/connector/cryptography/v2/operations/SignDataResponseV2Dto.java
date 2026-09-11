package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
 * Response envelope for {@code POST /v2/cryptographyProvider/operations/sign}. Signatures are returned inline on a sync
 * 200; on an async 202 they are absent and {@code operationMeta} is the tracking handle for the whole batch. A batch is
 * tracked as one operation and has one tracking handle.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "SignDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignDataResponseV2Dto {

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Signatures, correlated to the request items by identifier. "
                            + "Populated on sync 200; absent on async 202.",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED),
            minItems = 1)
    @Null(message = "signatures must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotEmpty(message = "signatures must contain at least one item for synchronous execution",
            groups = SynchronousResponse.class)
    @UniqueIdentifiers
    private List<@NotNull(message = "signatures must not contain null items") @Valid SignatureDataV2Dto> signatures;

    @ArraySchema(arraySchema = @Schema(description = "Connector-defined signing operation metadata. Present on async "
            + "202 as the tracking handle for the whole batch. Supply it by itself to /operations/sign/status and "
            + "/operations/sign/cancel. It must remain valid for the operation's entire tracking lifetime.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED), minItems = 1)
    @Null(message = "operationMeta must be absent for synchronous execution", groups = SynchronousResponse.class)
    @NotEmpty(message = "operationMeta must contain at least one item for asynchronous execution",
            groups = AsynchronousResponse.class)
    private List<@NotNull(
            message = "operationMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> operationMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 sign response property: " + property);
    }
}

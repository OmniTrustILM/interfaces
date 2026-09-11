package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
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
 * Response envelope specific to secret-key creation.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "SecretKeyDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class SecretKeyDataResponseV2Dto extends KeyCreationResponseV2Dto {

    @Override
    @Schema(description = "Type of key requested and returned", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = KeyRequestType.Codes.SECRET)
    public KeyRequestType getKeyRequestType() {
        return KeyRequestType.SECRET;
    }

    @Valid
    @Null(message = "keyData must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotNull(message = "keyData is required for synchronous execution", groups = SynchronousResponse.class)
    @Schema(description = "Created secret-key descriptor. Absent on an asynchronous 202 response.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SecretKeyDataV2Dto keyData;

    @ArraySchema(arraySchema = @Schema(description = "Connector-defined key handle. Present on a synchronous 200 "
            + "response. Metadata must identify the key durably—it must remain valid across connector restarts and "
            + "sessions; ephemeral handles must not be used.", requiredMode = Schema.RequiredMode.NOT_REQUIRED),
            minItems = 1)
    @Null(message = "keyMeta must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotEmpty(message = "keyMeta must contain at least one item for synchronous execution",
            groups = SynchronousResponse.class)
    private List<@NotNull(
            message = "keyMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported secret-key response property: " + property);
    }
}

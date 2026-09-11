package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Common response for a synchronous or asynchronous key operation. */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "KeyOperationResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class KeyOperationResponseV2Dto {

    @ArraySchema(arraySchema = @Schema(description = "Connector-defined operation tracking metadata. Required and "
            + "non-empty in the initial response accepting asynchronous execution. Absent from a synchronous "
            + "response and from a completed result nested in a status response. This handle must remain valid for the "
            + "operation's entire tracking lifetime.", requiredMode = Schema.RequiredMode.NOT_REQUIRED), minItems = 1)
    @Null(message = "operationMeta must be absent for synchronous execution", groups = SynchronousResponse.class)
    @NotEmpty(message = "operationMeta must contain at least one item for asynchronous execution",
            groups = AsynchronousResponse.class)
    private List<@NotNull(
            message = "operationMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> operationMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 key-operation response property: " + property);
    }
}

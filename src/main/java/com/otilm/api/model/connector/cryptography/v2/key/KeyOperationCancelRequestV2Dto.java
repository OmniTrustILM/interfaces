package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for the V2 key-operation cancellation endpoints.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class KeyOperationCancelRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    @Schema(description = "Connector-defined metadata returned in the original 202 Accepted response",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "operationMeta is required and must not be empty")
    private List<@NotNull @Valid MetadataAttributeV2> operationMeta;
}

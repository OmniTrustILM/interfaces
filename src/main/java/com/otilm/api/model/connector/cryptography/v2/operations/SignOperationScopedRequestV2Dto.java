package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Context for polling or cancelling an asynchronous sign operation. The metadata is the tracking handle
 * returned in the original {@code 202 Accepted} response.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SignOperationScopedRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Connector-defined signing operation metadata returned in the original sign data "
            + "202 Accepted response",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "signOperationMeta is required and must not be empty")
    private List<@NotNull @Valid MetadataAttributeV2> signOperationMeta;
}

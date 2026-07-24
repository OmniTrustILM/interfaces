package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureResponseDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response envelope for {@code POST /v2/cryptographyProvider/operations/sign}. Signatures are returned inline
 * on a sync 200; on an async 202 they are absent and {@code signOperationMeta} is the tracking handle for the
 * whole batch. A batch is tracked as one operation and has one tracking handle.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "SignDataResponseV2Dto")
public class SignDataResponseV2Dto {

    @Schema(description = "Signatures, correlated to the request items by identifier. Populated on sync 200; "
            + "null on async 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@Valid SignatureResponseDataV2Dto> signatures;

    @Schema(description = "Connector-defined signing operation metadata. Present on async 202 as the tracking "
            + "handle for the whole batch. Supply it to /operations/sign/status and /operations/sign/cancel.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> signOperationMeta;
}

package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/sign}. Asynchronous signing has no caller-supplied
 * idempotency identifier: if the accepted response is lost, its tracking handle cannot be recovered and retrying starts
 * a new batch.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SignDataRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Caller-selected execution mode. The connector must not switch modes implicitly.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "executionMode is required")
    private OperationExecutionMode executionMode;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "How the connector treats each data item. Absent means data, and callers omit the field for "
                    + "data mode because released connectors refuse unknown properties. Under prehashedDigest each "
                    + "item is already the digest the selected signature algorithm fixes and is never hashed "
                    + "again: RSA PKCS#1 v1.5 wraps it in a DigestInfo per RFC 8017 section 9.2 with explicit NULL "
                    + "parameters; RSA-PSS and ECDSA sign it with the parameters the signature algorithm code "
                    + "fixes. prehashedDigest is defined for RSA PKCS#1 v1.5, RSA-PSS and ECDSA signature "
                    + "algorithm codes only: any other code answers 422 PARAMETER_UNSUPPORTED, and an item whose "
                    + "length is not the output length of the fixed digest answers 422 VALIDATION_FAILED. A "
                    + "connector that does not implement the mode answers 422 PARAMETER_UNSUPPORTED and never "
                    + "ignores the field; one that implements it advertises the prehashedSigning feature flag.")
    private SignatureInputMode signatureInputMode;

    @Schema(description = "Batch-wide signature settings. Must include exactly one v3 string attribute named "
            + "signatureAlgorithm with UUID 9180267f-c82f-4b7b-8160-d2363d813869, selecting exactly one "
            + "signature algorithm code offered by the signing attribute schema.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required")
    private List<@NotNull(
            message = "signatureAttributes must not contain null items") RequestAttribute> signatureAttributes;

    @Schema(description = "Data to be signed", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data must contain at least one item")
    @UniqueIdentifiers
    private List<@NotNull(message = "data must not contain null items") @Valid SignatureDataV2Dto> data;
}

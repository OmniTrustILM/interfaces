package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/sign/algorithm}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "SignatureAlgorithmRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignatureAlgorithmRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "The signature attributes the caller intends to sign with, drawn from the connector's own "
            + "/sign/attributes schema.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(
            message = "signatureAttributes must not contain null items") RequestAttribute> signatureAttributes;
}

package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.validation.NullableNotBlank;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.validation.OneSignerSource;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@OneSignerSource
@Schema(name = "CreateCertificateRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CreateCertificateRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "DER CertificationRequestInfo the platform requests, holding the subject, the key's "
            + "public key and the requested extensions.", format = "byte", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "certificationRequestInfo is required and must not be empty")
    @ToString.Exclude
    private byte[] certificationRequestInfo;

    @Schema(description = "Signature algorithm of the certificate request, selected from the certificate "
            + "request attribute listing.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAlgorithm is required")
    private SignatureAlgorithm signatureAlgorithm;

    @Schema(description = "Signer the key is bound to, as the connector returned it. "
            + "Present only for a bound key, such as a rekeyed key or a key bound at creation.")
    @NullableNotBlank(message = "signer must not be blank")
    private String signer;

    @ArraySchema(minItems = 1,
            arraySchema = @Schema(description = "Values of the attributes from the signer attribute listing, "
                    + "each a v3 request attribute. Present only for an unbound key."))
    @Size(min = 1, message = "signerAttributes must not be empty")
    private List<@NotNull(message = "signerAttributes must not contain null items") RequestAttribute> signerAttributes;
}

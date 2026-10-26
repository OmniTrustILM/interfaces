package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CertificateIssuedRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CertificateIssuedRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "DER X.509 certificate issued for the key", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "certificate is required and must not be empty")
    @ToString.Exclude
    private byte[] certificate;

    @ArraySchema(arraySchema = @Schema(description = "Issuing chain in DER, starting with the certificate's issuer. "
            + "Absent when the platform holds none."))
    @ToString.Exclude
    private List<byte @NotNull(message = "chain must not contain null items") []> chain;

    @Schema(description = "Platform identifier of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "certificateUuid is required")
    private UUID certificateUuid;
}

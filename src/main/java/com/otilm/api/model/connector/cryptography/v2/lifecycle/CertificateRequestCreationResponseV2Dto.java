package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "CertificateRequestCreationResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CertificateRequestCreationResponseV2Dto {

    @Schema(description = "DER PKCS#10 CertificationRequest, signed with the requested algorithm. "
            + "It carries the requested subject and the request info's public key in the same encoding.",
            format = "byte", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "pkcs10 is required and must not be empty")
    @ToString.Exclude
    private byte[] pkcs10;

    @Schema(description = "Canonical signer the key is bound to. The connector derives it from the signer attributes "
            + "on the first request. Later requests return it unchanged.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "signer is required")
    private String signer;

    @ArraySchema(arraySchema = @Schema(
            description = "Steps the connector took during this call, in order. Absent when it took none."))
    private List<@NotNull(message = "events must not contain null items") @Valid OperationEventV2Dto> events;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException(
                "Unsupported v2 certificate request creation response property: " + property);
    }
}

package com.otilm.api.model.connector.signatures.validation;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignatureValidationRequestDto {

    @Schema(
            description = "Signed object to validate (e.g. TSA TimeStampToken, CAdES blob, raw signature bytes)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] signedObject;

    @Schema(
            description = "Original data whose integrity the signed object protects. " +
                    "May be omitted if the signed object already contains the original data (e.g. encapsulated CMS, PDF).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private byte[] originalData;

    @Schema(
            description = "Validation parameters (e.g. accepted policy OIDs, hash algorithm, nonce)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> validationAttributes;
}

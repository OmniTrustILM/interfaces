package com.otilm.api.model.connector.signatures.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignatureValidationResultDto {

    @Schema(
            description = "Validation status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ValidationStatus status;

    @Schema(
            description = "Human-readable reason when validation fails",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String reason;

    @Schema(
            description = "Optional validation report from the connector (e.g. XML or JSON report). " +
                    "Must be set together with dataContentType.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private byte[] data;

    @Schema(
            description = "MIME type of data. Must be set if and only if data is set.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String dataContentType;
}

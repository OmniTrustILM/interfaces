package com.czertainly.api.model.core.signing.digitalsignature;

import com.czertainly.api.model.core.signing.SignatureLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(
        name = "DigitalSignatureValidationResultDto",
        description = "Result of a Digital Signature validation operation. " +
                "Validation checks the cryptographic integrity of the signature, the validity of the " +
                "signing certificate chain, and the revocation status of all involved certificates " +
                "at the time of signing."
)
public class DigitalSignatureValidationResultDto {

    @Schema(
            description = "Whether the Digital Signature is cryptographically valid and all validation checks passed",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean valid;

    @Schema(
            description = "The highest ETSI conformance level that was successfully validated. " +
                    "Null if the signature could not be validated at any level.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private SignatureLevel validatedLevel;

    @Schema(
            description = "The time at which validation was performed",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ZonedDateTime validationTime;

    @Schema(
            description = "List of validation issues, warnings, or errors encountered during validation. " +
                    "May be non-empty even when valid is true (warnings). " +
                    "Always non-empty when valid is false.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<String> issues = new ArrayList<>();
}

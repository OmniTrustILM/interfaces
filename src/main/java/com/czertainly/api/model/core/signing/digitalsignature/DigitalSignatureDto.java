package com.czertainly.api.model.core.signing.digitalsignature;

import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Schema(name = "DigitalSignatureDto", description = "Digital Signature detail.")
public class DigitalSignatureDto {

    @Schema(
            description = "UUID of the Digital Signature",
            examples = {"7b55ge1c-844f-11dc-a8a3-0242ac120002"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID uuid;

    @Schema(description = "Signature Profile used to produce this Digital Signature", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureProfileListDto signatureProfile;

    @Schema(
            description = "Time at which the Digital Signature was produced. Represents the signing time as recorded by the signing operation.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ZonedDateTime signingTime;

    @Schema(
            description = "Signed data as a Base64-encoded byte array. " +
                    "Contains the raw output of the signing operation (e.g. a CMS SignedData structure). " +
                    "May be null if the signed data was not retained or has not been requested.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private byte[] signedData;
}

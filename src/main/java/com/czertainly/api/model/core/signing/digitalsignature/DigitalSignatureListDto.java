package com.czertainly.api.model.core.signing.digitalsignature;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "DigitalSignatureListDto", description = "Digital Signature details for listing")
public class DigitalSignatureListDto extends NameAndUuidDto {

    @Schema(description = "Signature Profile used to produce this Digital Signature", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureProfileListDto signatureProfile;

    @Schema(
            description = "Claimed signing time embedded in the signature structure by the signing operation. " +
                    "This is the local time reported by the signer and may not be trusted unless " +
                    "corroborated by a timestamp token (see signingProtocol).",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ZonedDateTime signingTime;

    @Schema(
            description = "Server time at which the Digital Signature record was created in the system. " +
                    "This timestamp is set by the platform and is independent of the cryptographic signing time.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ZonedDateTime createdAt;
}

package com.czertainly.api.model.core.signing.signingrecord;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.client.signing.profile.SigningProfileListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "SigningRecordDto", description = "Signing Record detail")
public class SigningRecordDto extends NameAndUuidDto {

    @Schema(description = "Signing Profile used to produce this Signing Record", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningProfileListDto signingProfile;

    @Schema(
            description = "Claimed signing time embedded in the signature structure by the signing operation. " +
                    "This is the local time reported by the signer and may not be trusted unless " +
                    "corroborated by a timestamp token (see signingProtocol).",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Instant signingTime;

    @Schema(
            description = "Server time at which the Signing Record was created in the system. " +
                    "This timestamp is set by the platform and is independent of the cryptographic signing time.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Instant createdAt;

    @Schema(
            description = "Raw signature value as a byte array (e.g. the DER-encoded CMS SignedData structure " +
                    "for CAdES, or the detached XML signature element for XAdES). " +
                    "May be null if the signature value was not retained by the platform.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private byte[] signatureValue;

    @Schema(description = "Signed document bytes (Base64-encoded), if recorded",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private byte[] signedDocument;

    @Schema(description = "Data-to-be-signed bytes (Base64-encoded), if recorded",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private byte[] dtbs;

    @Schema(description = "Captured request metadata (JSON), if recorded",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String requestMetadataJson;

    @Schema(description = "Time the signed document was first served via CSC retrieval, if any",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant signedDocumentRetrievedAt;
}

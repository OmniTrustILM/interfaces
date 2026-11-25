package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "SignRawResult", description = "Signature of one request item")
public class SignRawResultDto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier of the corresponding request item", examples = {"installer-x64"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String identifier;

    @Schema(description = "UUID of this item's signing record; absent when the Signing Profile's record policy "
            + "disables recording, or when a `best_effort` record could not be staged. A `best_effort` record may "
            + "still be lost after it is staged.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signingRecordUuid;

    @ToString.Exclude
    @Schema(description = "Signature over the item's `data`, base64-encoded in JSON. An ECDSA signature is the "
            + "DER-encoded sequence of r and s, not the concatenation of r and s that JWS uses.", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] signature;
}

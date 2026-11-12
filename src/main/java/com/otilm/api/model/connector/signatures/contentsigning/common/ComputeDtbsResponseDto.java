package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Result of {@code computeDtbs}: the bytes to sign, the digest of the document they commit to, and the connector's
 * working state for the next call.
 */
@Getter
@Setter
@ToString
@Schema(name = "ComputeDtbsResponse",
        description = "Data-to-be-signed bytes, the documentDigest they commit to, and the formattingContext that "
                + "pairs with them")
public class ComputeDtbsResponseDto {

    @ToString.Exclude
    @NotNull(message = "dtbs is required")
    @Schema(description = "The data-to-be-signed bytes, base64-encoded in JSON. They must embed the digest of the "
            + "document the user authorized.", requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] dtbs;

    @ToString.Exclude
    @NotNull(message = "documentDigest is required")
    @Schema(description = "Digest of the document the connector embedded in dtbs, base64-encoded in JSON.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] documentDigest;

    @NotNull(message = "documentDigestAlgorithm is required")
    @Schema(description = "Algorithm that produced documentDigest.", requiredMode = Schema.RequiredMode.REQUIRED)
    private DigestAlgorithm documentDigestAlgorithm;

    @ToString.Exclude
    @NotNull(message = "formattingContext is required")
    @Schema(description = "Whatever the connector needs to complete this signature once the signature value comes "
            + "back, base64-encoded in JSON. The platform replays it verbatim to embedSignatureValue and never stores "
            + "it.", requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] formattingContext;
}

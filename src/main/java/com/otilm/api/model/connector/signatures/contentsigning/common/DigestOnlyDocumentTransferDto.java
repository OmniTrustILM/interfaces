package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;

/**
 * The digest-only arm of {@link DocumentTransferDto}: only the document's digest travels, so the customer document
 * never enters the platform.
 *
 * <p>
 * The digest and the algorithm that produced it are both required, because a digest cannot be interpreted without
 * knowing which algorithm produced it. Making them components of the same arm is what keeps the pair inseparable.
 * </p>
 */
@JsonIgnoreProperties(value = "transferMode", allowGetters = true)
@Schema(name = "DigestOnlyDocumentTransfer", description = "Document transport carrying only the document's digest")
public record DigestOnlyDocumentTransferDto(
        @NotNull(message = "documentDigest is required") @Schema(
                description = "Digest of the document, where the "
                        + "document itself never enters the platform. Base64-encoded in JSON.",
                requiredMode = Schema.RequiredMode.REQUIRED) byte[] documentDigest,
        @NotNull(message = "digestAlgorithm is required") @Schema(
                description = "Algorithm that produced documentDigest. The enum spans every algorithm the platform "
                        + "knows, including collision-broken ones. Where this transfer is the document of a "
                        + "computeDtbs request, the platform pins it to the digest that request's signatureAlgorithm "
                        + "commits to, and checks it before it calls.",
                requiredMode = Schema.RequiredMode.REQUIRED) DigestAlgorithm digestAlgorithm)
        implements
            DocumentTransferDto {

    @Override
    public DocumentTransferMode getTransferMode() {
        return DocumentTransferMode.DIGEST_ONLY;
    }

    /**
     * A digest whose length contradicts its algorithm cannot have come from any document. A null component is left to
     * its own {@code @NotNull}.
     */
    @JsonIgnore
    @AssertTrue(message = "documentDigest length must match the digest size of digestAlgorithm")
    @Schema(hidden = true)
    public boolean isDigestLengthConsistentWithAlgorithm() {
        return documentDigest == null || digestAlgorithm == null
                || documentDigest.length == digestAlgorithm.getDigestSizeBytes();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DigestOnlyDocumentTransferDto transfer
                && Arrays.equals(documentDigest, transfer.documentDigest)
                && digestAlgorithm == transfer.digestAlgorithm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(documentDigest), digestAlgorithm);
    }

    /** A digest is not customer content, so it stays legible in a log line rather than being rendered as an address. */
    @Override
    public String toString() {
        return "DigestOnlyDocumentTransferDto[documentDigest=" + Arrays.toString(documentDigest) + ", digestAlgorithm="
                + digestAlgorithm + "]";
    }
}

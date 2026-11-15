package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Everything the connector needs to build the data-to-be-signed bytes arrives here, including {@code signingTime}. The
 * connector never reads its own clock. A signature's time must be the one the platform recorded, not the one a
 * connector replica observed.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ComputeDtbsRequest", description = "Request to compute the data-to-be-signed bytes")
public class ComputeDtbsRequestDto extends ContentSigningFormattingRequestDto {

    @Valid
    @NotNull(message = "document is required")
    @Schema(description = "The document to sign, inline or as a digest", requiredMode = Schema.RequiredMode.REQUIRED)
    private DocumentTransferDto document;

    @NotEmpty(message = "signerCertificateChain is required")
    @Schema(description = "Certificate chain whose first element is the signer certificate. Each certificate is a "
            + "DER-encoded X.509 certificate, base64-encoded in JSON.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull(
            message = "signerCertificateChain must not contain null items") byte[]> signerCertificateChain;

    @NotNull(message = "signingTime is required")
    @Schema(description = "Signing time supplied by the platform. The connector puts this value into the signature "
            + "and never substitutes its own clock.", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime signingTime;

    @NotNull(message = "signatureAlgorithm is required")
    @Schema(description = "Signature algorithm the platform's signer will use. A connector MUST build the "
            + "data-to-be-signed bytes for exactly this algorithm and MUST NOT substitute one of its own. A "
            + "connector that cannot format for this algorithm MUST answer 422 with errorCode "
            + "PARAMETER_UNSUPPORTED, naming the algorithms it does support.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureAlgorithm signatureAlgorithm;

    @ToString.Exclude
    @Schema(description = "Values the signing request sets for request attributes, each allowed by the Signing "
            + "Profile and checked against its definition. The connector must merge them over `formattingAttributes`, "
            + "the request winning, and carry the merged result to embedSignatureValue in `formattingContext`. May be "
            + "an empty list, but must be present.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "requestAttributes may be an empty list, but must be present")
    private List<@NotNull(
            message = "requestAttributes must not contain null items") RequestAttribute> requestAttributes;
}

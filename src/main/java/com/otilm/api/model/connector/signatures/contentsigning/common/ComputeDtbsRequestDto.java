package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.connector.signatures.contentsigning.cades.CadesComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.jades.JadesComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.pades.PadesComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.xades.XadesComputeDtbsRequestDto;
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
 * Abstract base of the {@code computeDtbs} request, the one operation whose payload varies by family.
 *
 * <p>
 * Everything the connector needs to build the data-to-be-signed bytes arrives here, including {@code signingTime}. The
 * connector never reads its own clock. A signature's time must be the one the platform recorded, not the one a
 * connector replica observed.
 * </p>
 *
 * <p>
 * The union resolves on the inherited {@code family} property. {@link ComputeDtbsInterface} explains why the
 * discriminator sits inside the object. Each subtype fixes that property to its own family and carries whatever typed
 * parameters that family defines.
 * </p>
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "family",
        visible = true)
@JsonSubTypes({
        @Type(value = PadesComputeDtbsRequestDto.class, name = SignatureFamily.Codes.PADES),
        @Type(value = XadesComputeDtbsRequestDto.class, name = SignatureFamily.Codes.XADES),
        @Type(value = CadesComputeDtbsRequestDto.class, name = SignatureFamily.Codes.CADES),
        @Type(value = JadesComputeDtbsRequestDto.class, name = SignatureFamily.Codes.JADES)})
@Schema(implementation = ComputeDtbsInterface.class)
public abstract class ComputeDtbsRequestDto extends ContentSigningFormattingRequestDto implements ComputeDtbsInterface {

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

    protected ComputeDtbsRequestDto(SignatureFamily family) {
        super.setFamily(family);
    }

    /**
     * Refuses to repoint the discriminator.
     */
    @Override
    public void setFamily(SignatureFamily family) {
        if (family != getFamily()) {
            throw new IllegalArgumentException("family is fixed to " + getFamily() + " by " + getClass().getSimpleName()
                    + " and cannot be set to " + family + "; construct the subtype for the family you want");
        }
    }
}

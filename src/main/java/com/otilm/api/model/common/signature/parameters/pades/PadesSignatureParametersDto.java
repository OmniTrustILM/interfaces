package com.otilm.api.model.common.signature.parameters.pades;

import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParameterSink;
import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The PAdES signature parameters a caller may influence, each gated by the group {@code describe} reports for it.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "PadesSignatureParameters",
        description = "Signature parameters for the PAdES family. Every parameter is optional and independent. The "
                + "field that declares this object says which values it carries and how absence is resolved.")
public class PadesSignatureParametersDto extends SignatureParametersDto {

    @ToString.Exclude
    @Size(max = 512, message = "reason must be at most 512 characters")
    @NullableNotBlank(message = "reason must not be blank if provided")
    @Schema(description = "Why the document was signed, recorded as the /Reason entry of the signature dictionary",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Contract approval")
    private String reason;

    @ToString.Exclude
    @Size(max = 512, message = "location must be at most 512 characters")
    @NullableNotBlank(message = "location must not be blank if provided")
    @Schema(description = "Where the document was signed, recorded as the /Location entry of the signature dictionary",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Prague")
    private String location;

    @ToString.Exclude
    @Size(max = 512, message = "contactInfo must be at most 512 characters")
    @NullableNotBlank(message = "contactInfo must not be blank if provided")
    @Schema(description = "How to reach the signer, recorded as the /ContactInfo entry of the signature dictionary",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "jane.doe@example.com")
    private String contactInfo;

    @ToString.Exclude
    @Size(max = 512, message = "signerName must be at most 512 characters")
    @NullableNotBlank(message = "signerName must not be blank if provided")
    @Schema(description = "Name shown as the signer, recorded as the /Name entry of the signature dictionary. Absent "
            + "means the name is derived from the signing certificate.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Jane Doe")
    private String signerName;

    @Schema(description = "Whether the signature certifies the document or approves a revision of it. Absent is "
            + "unspecified: on a request the Signing Profile's default decides, and in the effective parameters it "
            + "means revision. Certification is never inferred.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PadesSignatureScope signatureScope;

    @Schema(description = "Commitment the signer makes, bound into the signature as a signed attribute",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CommitmentType commitmentType;

    @ToString.Exclude
    @Size(min = 1, max = 10, message = "claimedRoles must contain between 1 and 10 roles")
    @Schema(description = "Roles the signer claims, bound into the signature as a signed attribute. Claimed only: a "
            + "certified role would be an attribute certificate, which this contract does not carry. Omit the field "
            + "to inherit; an empty list is refused.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "[\"Head of Legal\"]")
    private List<@NotNull(message = "claimedRoles must not contain null items") @Size(max = 256,
            message = "each claimed role must be at most 256 characters") @NullableNotBlank(
                    message = "each claimed role must not be blank") String> claimedRoles;

    @Valid
    @Schema(description = "The visible signature drawn on the page: what it shows, where it goes and how it is drawn",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PadesVisibleSignatureDto visibleSignature;

    public PadesSignatureParametersDto() {
        super(SignatureFamily.PADES);
    }

    @Override
    public void describe(SignatureParameterSink sink) {
        sink.leaf("reason", SignatureParameterGroup.SIGNATURE_CONTEXT, reason);
        sink.leaf("location", SignatureParameterGroup.SIGNATURE_CONTEXT, location);
        sink.leaf("contactInfo", SignatureParameterGroup.SIGNATURE_CONTEXT, contactInfo);
        sink.leaf("signerName", SignatureParameterGroup.SIGNER_IDENTITY, signerName);
        sink.leaf("signatureScope", SignatureParameterGroup.SIGNATURE_SCOPE, signatureScope);
        sink.leaf("commitmentType", SignatureParameterGroup.SIGNED_ATTRIBUTES, commitmentType);
        sink.leaf("claimedRoles", SignatureParameterGroup.SIGNED_ATTRIBUTES, claimedRoles);
        sink.container("visibleSignature", visibleSignature);
    }
}

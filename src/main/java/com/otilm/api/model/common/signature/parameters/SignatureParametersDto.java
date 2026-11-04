package com.otilm.api.model.common.signature.parameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract base of the signature parameters a caller may influence.
 *
 * <p>
 * One representation serves three surfaces: the signing request, the Signing Profile's defaults, and the object the
 * connector receives. The merge is therefore well typed by construction, and the client-facing and connector-facing
 * shapes cannot drift apart.
 * </p>
 *
 * <p>
 * Every parameter is optional at every one of those surfaces, because each is an override of the value below it.
 * </p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "family",
        visible = true)
@JsonSubTypes({@Type(value = PadesSignatureParametersDto.class, name = SignatureFamily.Codes.PADES)})
@Schema(implementation = SignatureParametersInterface.class)
public abstract class SignatureParametersDto implements SignatureParametersInterface, SignatureParameterContainer {

    @NotNull(message = "family is required")
    @Schema(description = "Signature family these parameters belong to, and the discriminator selecting the "
            + "family-specific shape", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureFamily family;

    protected SignatureParametersDto(SignatureFamily family) {
        this.family = family;
    }

    @Override
    public abstract void describe(SignatureParameterSink sink);

    /**
     * Refuses to repoint the discriminator.
     */
    public void setFamily(SignatureFamily family) {
        if (family != this.family) {
            throw new IllegalArgumentException("family is fixed to " + this.family + " by " + getClass().getSimpleName()
                    + " and cannot be set to " + family + "; construct the subtype for the family you want");
        }
    }
}

package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * OpenAPI schema for the polymorphic {@link SignatureParametersDto} hierarchy.
 *
 * <p>
 * The discriminator is the required {@code family} property on each subtype, not a wrapper. OpenAPI's
 * {@code discriminator} needs a property present in every {@code oneOf} subschema, which is why the {@code computeDtbs}
 * request does the same.
 * </p>
 */
@Schema(name = "SignatureParameters",
        description = "Signature parameters a caller may influence, in the shape shared by the signing request, the "
                + "Signing Profile's defaults and the connector's effective object. The required family property is "
                + "the discriminator selecting the family-specific shape.",
        type = "object", discriminatorProperty = "family",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SignatureFamily.Codes.PADES, schema = PadesSignatureParametersDto.class)},
        oneOf = {PadesSignatureParametersDto.class})
public interface SignatureParametersInterface extends Serializable {

    @Schema(description = "Signature family these parameters belong to", requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {SignatureFamily.Codes.PADES})
    SignatureFamily getFamily();
}

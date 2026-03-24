package com.czertainly.api.model.core.signing.signatureprofile;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "SigningSchemeRequestInterface",
        description = "Signing Scheme request configuration dependent on signing scheme",
        type = "object",
        discriminatorProperty = "signingScheme",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningScheme.Codes.DELEGATED, schema = DelegatedSigningRequestDto.class),
                @DiscriminatorMapping(value = SigningScheme.Codes.MANAGED, schema = ManagedSigningRequestDto.class),
        },
        oneOf = {
                DelegatedSigningRequestDto.class,
                ManagedSigningRequestDto.class,
        })
public interface SigningSchemeRequestInterface extends Serializable {
    @Schema(description = "Signing scheme", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningScheme.Codes.MANAGED})
    SigningScheme getSigningScheme();
}

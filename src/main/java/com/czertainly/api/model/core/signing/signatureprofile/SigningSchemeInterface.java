package com.czertainly.api.model.core.signing.signatureprofile;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "SigningSchemeInterface",
        description = "Signing Scheme configuration dependent on signing scheme",
        type = "object",
        discriminatorProperty = "signingScheme",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningScheme.Codes.DELEGATED, schema = DelegatedSigningDto.class),
                @DiscriminatorMapping(value = SigningScheme.Codes.MANAGED, schema = ManagedSigningDto.class),
        },
        oneOf = {
                DelegatedSigningDto.class,
                ManagedSigningDto.class,
        })
public interface SigningSchemeInterface extends Serializable {
    @Schema(description = "Signing scheme", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningScheme.Codes.MANAGED})
    SigningScheme getSigningScheme();
}

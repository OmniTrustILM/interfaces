package com.czertainly.api.model.core.signing.signatureprofile.scheme;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "ManagedSigningSchemeInterface",
        description = "Managed signing configuration dependent on the managed signing type",
        type = "object",
        discriminatorProperty = "managedSigningType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = ManagedSigningType.Codes.STATIC_KEY,   schema = StaticKeyManagedSigningDto.class),
                @DiscriminatorMapping(value = ManagedSigningType.Codes.ONE_TIME_KEY, schema = OneTimeKeyManagedSigningDto.class),
        },
        oneOf = {
                StaticKeyManagedSigningDto.class,
                OneTimeKeyManagedSigningDto.class,
        })
public interface ManagedSigningSchemeInterface extends Serializable {
    @Schema(description = "Managed signing type", requiredMode = Schema.RequiredMode.REQUIRED)
    ManagedSigningType getManagedSigningType();
}

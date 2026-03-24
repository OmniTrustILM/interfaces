package com.czertainly.api.model.core.signing.signatureprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ManagedSigningRequestDto", description = "Request to configure managed signing with Token Profile and Cryptographic Key")
public class ManagedSigningRequestDto extends SigningSchemeRequestDto {

    @NotNull
    @Schema(description = "UUID of the Token Profile to use for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID tokenProfileUuid;

    @NotNull
    @Schema(description = "UUID of the Cryptographic Key to use for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cryptographicKeyUuid;

    public ManagedSigningRequestDto() {
        super(SigningScheme.MANAGED);
    }
}

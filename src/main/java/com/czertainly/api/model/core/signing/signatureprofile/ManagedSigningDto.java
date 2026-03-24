package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ManagedSigningDto", description = "Managed signing configuration with Token Profile and Cryptographic Key references")
public class ManagedSigningDto extends SigningSchemeDto {

    @NotNull
    @Schema(description = "Reference to the Token Profile used for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto tokenProfile;

    @NotNull
    @Schema(description = "Reference to the Cryptographic Key used for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto cryptographicKey;

    public ManagedSigningDto() {
        super(SigningScheme.MANAGED);
    }
}

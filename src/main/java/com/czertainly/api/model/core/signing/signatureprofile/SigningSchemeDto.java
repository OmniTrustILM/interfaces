package com.czertainly.api.model.core.signing.signatureprofile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "signingScheme", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ManagedSigningDto.class, name = SigningScheme.Codes.MANAGED),
        @JsonSubTypes.Type(value = DelegatedSigningDto.class, name = SigningScheme.Codes.DELEGATED),
})
@Schema(implementation = SigningSchemeInterface.class)
public abstract class SigningSchemeDto implements SigningSchemeInterface {

    @NotNull
    @Schema(description = "Signing scheme", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningScheme.Codes.MANAGED})
    private final SigningScheme signingScheme;

    protected SigningSchemeDto(SigningScheme signingScheme) {
        this.signingScheme = signingScheme;
    }
}

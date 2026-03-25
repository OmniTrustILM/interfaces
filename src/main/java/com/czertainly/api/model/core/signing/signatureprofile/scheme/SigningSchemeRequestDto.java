package com.czertainly.api.model.core.signing.signatureprofile.scheme;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "signingScheme", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ManagedSigningRequestDto.class, name = SigningScheme.Codes.MANAGED),
        @JsonSubTypes.Type(value = DelegatedSigningRequestDto.class, name = SigningScheme.Codes.DELEGATED),
})
@Schema(implementation = SigningSchemeRequestInterface.class)
public abstract class SigningSchemeRequestDto implements SigningSchemeRequestInterface {

    @NotNull
    @Schema(description = "Signing scheme", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningScheme.Codes.MANAGED})
    private final SigningScheme signingScheme;

    protected SigningSchemeRequestDto(SigningScheme signingScheme) {
        this.signingScheme = signingScheme;
    }
}

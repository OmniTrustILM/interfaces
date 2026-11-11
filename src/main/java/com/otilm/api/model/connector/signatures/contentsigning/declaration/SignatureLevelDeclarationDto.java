package com.otilm.api.model.connector.signatures.contentsigning.declaration;

import com.otilm.api.model.common.signature.SignatureLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SignatureLevelDeclaration", description = "Level a family reaches, with the name the family gives it")
public class SignatureLevelDeclarationDto {

    @NotNull
    @Schema(description = "Level reached", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureLevel level;

    @NotBlank
    @Schema(description = "Name the family gives the level", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PAdES-B-T")
    private String name;
}

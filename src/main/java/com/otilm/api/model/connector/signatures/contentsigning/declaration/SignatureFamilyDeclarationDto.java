package com.otilm.api.model.connector.signatures.contentsigning.declaration;

import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.validation.LevelLadder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(name = "SignatureFamilyDeclaration", description = "Signature family a formatting connector serves")
public class SignatureFamilyDeclarationDto {

    @NotNull
    @Schema(description = "Code of the family", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureFamily code;

    @NotBlank
    @Schema(description = "Name of the family shown to an operator", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PAdES")
    private String label;

    @Schema(description = "Standard and document format the family follows",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "PDF Advanced Electronic Signatures, ETSI EN 319 142")
    private String description;

    @NotEmpty
    @Valid
    @LevelLadder
    @Schema(description = "Levels the family reaches, from SIGNED upwards without a gap; the last one is the "
            + "family's ceiling", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull SignatureLevelDeclarationDto> levels;

    @NotEmpty
    @Schema(description = "Transfer modes the family signs a document from",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<@NotNull DocumentTransferMode> transferModes;

    @Size(min = 1, message = "must not be empty when present")
    @Schema(description = "Signature algorithms the family supports, at least one; absent when the family accepts "
            + "every algorithm the platform can sign with", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<@NotNull SignatureAlgorithm> signatureAlgorithms;
}

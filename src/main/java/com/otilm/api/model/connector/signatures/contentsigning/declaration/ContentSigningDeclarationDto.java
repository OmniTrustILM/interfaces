package com.otilm.api.model.connector.signatures.contentsigning.declaration;

import com.otilm.api.model.connector.signatures.contentsigning.declaration.validation.UniqueFamilyCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(name = "ContentSigningDeclaration",
        description = "Declaration of families served by a content-signing formatting connector; an object, so it can gain "
                + "connector-wide facts without a break")
public class ContentSigningDeclarationDto {

    @NotEmpty
    @Valid
    @UniqueFamilyCodes
    @Schema(description = "Families the connector serves, each with a unique code",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull SignatureFamilyDeclarationDto> families = new ArrayList<>();

    /** Written out rather than generated, so a connector-wide field added later keeps callers compiling. */
    public ContentSigningDeclarationDto(List<SignatureFamilyDeclarationDto> families) {
        this.families = families;
    }
}

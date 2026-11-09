package com.otilm.api.model.core.signing.parameters;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
@Schema(name = "SignatureParameterGroupFields",
        description = "Signature parameter group with the paths of the fields it governs")
public class SignatureParameterGroupFieldsDto {

    @Schema(description = "Group governing the fields", requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureParameterGroup group;

    @Schema(description = "Paths of the fields the group governs, in the order the family's parameters "
            + "declare them. A path joins property names with dots, from the family's signature parameters down to "
            + "the field.", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "[\"visibleSignature.placement.page\"]")
    private List<String> fields;
}

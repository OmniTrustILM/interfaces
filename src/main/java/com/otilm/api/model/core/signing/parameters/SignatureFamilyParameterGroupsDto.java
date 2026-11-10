package com.otilm.api.model.core.signing.parameters;

import com.otilm.api.model.common.signature.SignatureFamily;
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
@Schema(name = "SignatureFamilyParameterGroups",
        description = "Signature parameter groups of one signature family, each with the fields it governs")
public class SignatureFamilyParameterGroupsDto {

    @Schema(description = "Signature family whose parameters the groups govern",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureFamily family;

    @Schema(description = "Groups governing the family's parameters, in the order the `SignatureParameterGroup` "
            + "enum lists them", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SignatureParameterGroupFieldsDto> groups;
}

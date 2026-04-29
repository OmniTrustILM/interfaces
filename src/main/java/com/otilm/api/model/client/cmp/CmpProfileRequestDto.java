package com.otilm.api.model.client.cmp;

import com.otilm.api.model.client.cmp.validation.ValidCmpProfileName;
import com.otilm.api.model.core.cmp.CmpProfileVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CmpProfileRequestDto extends BaseCmpProfileRequestDto {

    @NotBlank(
            message = "Name of the CMP Profile is required"
    )
    @ValidCmpProfileName
    @Schema(
            description = "Name of the CMP Profile",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"Example CMP Profile"}
    )
    private String name;

    @NotNull
    @Schema(
            description = "Variant of the CMP Profile",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CmpProfileVariant variant;

}

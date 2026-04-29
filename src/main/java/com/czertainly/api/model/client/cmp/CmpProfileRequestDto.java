package com.czertainly.api.model.client.cmp;

import com.czertainly.api.model.common.validation.ValidName;
import com.czertainly.api.model.core.cmp.CmpProfileVariant;
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
    @ValidName
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

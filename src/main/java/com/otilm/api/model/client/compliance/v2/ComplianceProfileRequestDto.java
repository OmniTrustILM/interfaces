package com.otilm.api.model.client.compliance.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ComplianceProfileRequestDtoV2")
public class ComplianceProfileRequestDto extends ComplianceProfileUpdateRequestDto {

    @NotNull
    @NotBlank
    @Schema(description = "Name of the Compliance Profile", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Profile 1"})
    private String name;

}

package com.otilm.api.model.client.scep;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScepProfileRequestDto extends BaseScepProfileRequestDto {

    @Schema(
            description = "Name of the SCEP Profile",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"Profile Name 1"}
    )
    private String name;

}

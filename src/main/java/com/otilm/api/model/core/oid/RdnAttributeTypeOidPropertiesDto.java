package com.otilm.api.model.core.oid;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RdnAttributeTypeOidPropertiesDto implements AdditionalOidPropertiesDto {

    public static final String CODE_REGEX = "^[A-Za-z][A-Za-z0-9-]*$";
    public static final String CODE_VALIDATION_MESSAGE = "Code must start with a letter and contain only letters, numbers, and hyphens.";


    @Schema(description = "Code to be displayed in RDN string (e.g., CN for Common Name)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(
            regexp = CODE_REGEX,
            message = CODE_VALIDATION_MESSAGE
    )
    private String code;

    @Schema(description = "Alternative codes that can appear in RDN representing the same OID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotBlank @Pattern(
            regexp = CODE_REGEX,
            message = CODE_VALIDATION_MESSAGE
    ) String> altCodes = new ArrayList<>();

}

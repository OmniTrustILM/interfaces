package com.otilm.api.model.core.oid;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomOidEntryRequestDto extends CustomOidEntryUpdateRequestDto implements Serializable {

    @Schema(
            description = "Object Identifier (OID) in dot notation (e.g., 1.2.840.113549.1.1.1)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1.2.840.113549.1.1.1"
    )
    @NotBlank
    @Pattern(
            regexp = "^[0-2](\\.(0|[1-9]\\d*)){1,50}$",
            message = "Invalid OID format"
    )
    private String oid;

}


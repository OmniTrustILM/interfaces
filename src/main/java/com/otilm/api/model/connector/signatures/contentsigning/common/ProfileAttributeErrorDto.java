package com.otilm.api.model.connector.signatures.contentsigning.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(name = "ProfileAttributeError",
        description = "Problem the connector found with one attribute of a Signing Profile")
public class ProfileAttributeErrorDto {

    @NotBlank(message = "attributeName is required")
    @Schema(description = "Name of the attribute, as the connector's listing declares it",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "font")
    private String attributeName;

    @NotBlank(message = "reason is required")
    @Schema(description = "Reason the connector cannot use the value, for the operator to read",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "The font file cannot be parsed")
    private String reason;

    public ProfileAttributeErrorDto(String attributeName, String reason) {
        this.attributeName = attributeName;
        this.reason = reason;
    }
}

package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EarlyRenewalRequestDto {

    @Schema(description = "Period in seconds over which the early renewals are spread",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "3600", maximum = "2592000")
    @NotNull
    @Min(3600)
    @Max(2592000)
    private Long spreadSeconds;

    @Schema(description = "Absolute http(s) URL explaining the reason for the early renewal",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 2048, message = "Explanation URL must not exceed 2048 characters.")
    @Pattern(regexp = "^https?://\\S+$", message = "Explanation URL must be an absolute http(s) URL.")
    private String explanationUrl;
}

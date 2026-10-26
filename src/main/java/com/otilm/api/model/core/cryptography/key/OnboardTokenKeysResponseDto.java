package com.otilm.api.model.core.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(name = "OnboardTokenKeysResponseDto", description = "One result per selected key, in the order selected")
public class OnboardTokenKeysResponseDto {

    @Schema(description = "Results of the onboarding", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TokenKeyOnboardingResultDto> results = new ArrayList<>();
}

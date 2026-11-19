package com.otilm.api.model.connector.signatures.contentsigning.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ValidateProfileRequest",
        description = "Signing Profile values the connector judges as a whole before the profile is saved")
public class ValidateProfileRequestDto extends ContentSigningFormattingRequestDto {

    private static final String NO_BLANK_ALLOWED_NAMES = "allowedRequestAttributes must not contain blank names";

    @NotNull(message = "allowedRequestAttributes may be an empty set, but must be present")
    @Schema(description = "Names of the groups and ungrouped request attributes a signing request may set, as the "
            + "Signing Profile allows them. A value the profile leaves unset can come only from a request allowed to "
            + "set it. May be an empty set, but must be present.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<@NotBlank(message = NO_BLANK_ALLOWED_NAMES) String> allowedRequestAttributes;
}

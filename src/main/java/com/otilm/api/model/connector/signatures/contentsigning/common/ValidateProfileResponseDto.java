package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(name = "ValidateProfileResponse",
        description = "Connector's judgement of a Signing Profile: the problems it found and the transfer modes the "
                + "profile's values allow")
public class ValidateProfileResponseDto {

    private static final String NO_NULL_ERRORS = "errors must not contain null items";

    private static final String NO_NULL_TRANSFER_MODES = "transferModes must not contain null items";

    @NotNull(message = "errors may be an empty list, but must be present")
    @Valid
    @Schema(description = "Problems with the profile's values, each naming one attribute; empty when the profile is "
            + "valid", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull(message = NO_NULL_ERRORS) ProfileAttributeErrorDto> errors;

    @NotNull(message = "transferModes may be an empty set, but must be present")
    @Schema(description = "Transfer modes a signing request under this profile may send its document in, each among "
            + "the transfer modes the family declares; at least one when `errors` is empty",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<@NotNull(message = NO_NULL_TRANSFER_MODES) DocumentTransferMode> transferModes;

    /** Other repositories call this positionally. A generated constructor would change with every added field. */
    public ValidateProfileResponseDto(List<ProfileAttributeErrorDto> errors, Set<DocumentTransferMode> transferModes) {
        this.errors = errors;
        this.transferModes = transferModes;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "transferModes must name at least one mode when errors is empty")
    public boolean isTransferModeNamedForValidProfile() {
        if (errors == null || transferModes == null) {
            return true;
        }
        return !errors.isEmpty() || !transferModes.isEmpty();
    }
}

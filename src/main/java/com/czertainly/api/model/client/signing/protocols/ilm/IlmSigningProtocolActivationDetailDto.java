package com.czertainly.api.model.client.signing.protocols.ilm;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IlmSigningProtocolActivationDetailDto extends NameAndUuidDto {

    @Schema(
            description = "ILM Signing Protocol availability flag - true = activated; false = not activated",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean available;

    @Schema(
            description = "ILM Signing Protocol URL for signing",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "https://ilm.otilm.com/api/v1/protocols/ilm/signingProfile/signing-profile-1/sign"
    )
    private String signingUrl;
}

package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(name = "TspActivationDetailDto", description = "TSP activation detail")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TspActivationDetailDto extends NameAndUuidDto {

    @Schema(
            description = "TSP availability flag - true = activated; false = not activated",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean available;

    @Schema(
            description = "TSP URL for signing",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "https://ilm.otilm.com/api/v1/protocols/tsp/signingProfile/signing-profile-1/sign"
    )
    private String signingUrl;
}

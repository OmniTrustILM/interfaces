package com.otilm.api.model.core.cryptography.token;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TokenInstanceDto extends NameAndUuidDto {

    @Schema(
            description = "Status Of the Token Instance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TokenInstanceStatus status;

    @Schema(
            description = "Number of Token Profiles associated",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer tokenProfiles;

    @Schema(
            description = "Connector Name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String connectorName;

    @Schema(
            description = "Connector UUID",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String connectorUuid;

    @Schema(
            description = "Connector Kind",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String kind;

}


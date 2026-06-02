package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(name = "TspBasicCredentialDto", description = "Read-only view of a Basic credential on a TSP Profile.")
public class TspBasicCredentialDto {

    @Schema(description = "UUID of this credential", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Basic username", requiredMode = Schema.RequiredMode.REQUIRED, example = "svc-account")
    private String username;

    @Schema(description = "The real ILM user this credential authenticates as", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto mappedUser;
}

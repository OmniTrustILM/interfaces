package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(name = "TspBasicCredentialDto", description = "Read-only view of a Basic credential on a TSP Profile.")
public class TspBasicCredentialDto implements ObjectActionsAware {

    @Schema(description = "UUID of this credential", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Basic username", requiredMode = Schema.RequiredMode.REQUIRED, example = "svc-account")
    private String username;

    @Schema(description = "The real ILM user this credential authenticates as",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto mappedUser;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}

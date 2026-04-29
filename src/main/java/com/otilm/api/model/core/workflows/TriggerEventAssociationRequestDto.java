package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.other.ResourceEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TriggerEventAssociationRequestDto {

    @Schema(description = "Resource event", requiredMode = Schema.RequiredMode.REQUIRED)
    private ResourceEvent event;

    @Schema(description = "Event triggers association resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "Event triggers association object UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID objectUuid;

    @Schema(description = "List of triggers associated with event", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UUID> triggerUuids;

}

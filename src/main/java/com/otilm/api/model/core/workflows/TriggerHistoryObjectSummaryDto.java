package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.other.ResourceObjectDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class TriggerHistoryObjectSummaryDto {
    @Schema(description = "UUID of the object that the trigger has been evaluated on.")
    private UUID objectUuid;

    @Schema(description = "Reference UUID of the object that the trigger has been evaluated on.")
    private UUID referenceObjectUuid;

    @Schema(description = "The object whose page shows the evaluated object, when that object has no page of its own - "
            + "the host object of a comment, for instance. Absent when the evaluated object is navigable itself.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ResourceObjectDto hostObject;

    @Schema(description = "Was matched at least by one trigger.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean matched;

    @Schema(description = "Was matched by ignore trigger.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean ignored;

    @Schema(description = "List of records for each trigger that has been evaluated.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerHistoryObjectTriggerSummaryDto> triggers = new ArrayList<>();
}

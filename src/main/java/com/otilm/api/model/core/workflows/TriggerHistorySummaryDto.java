package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TriggerHistorySummaryDto {
    @Schema(description = "Resource of the object associated with triggers.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource associationResource;

    @Schema(description = "UUID of the object associated with triggers.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String associationObjectUuid;

    @Schema(description = "Resource of objects that triggers has been evaluated on.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource objectsResource;

    @Schema(description = "Number of objects evaluated.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsEvaluated;

    @Schema(description = "Number of objects matched at least by one trigger.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsMatched;

    @Schema(description = "Number of objects matched by ignore triggers.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsIgnored;

    @Schema(description = "List of history of objects that triggers has been evaluated on.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerHistoryObjectSummaryDto> objects = new ArrayList<>();
 }

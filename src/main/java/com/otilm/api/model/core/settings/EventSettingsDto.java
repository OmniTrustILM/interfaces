package com.otilm.api.model.core.settings;

import com.otilm.api.model.core.other.ResourceEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing settings of single event")
public class EventSettingsDto {

    @NotNull
    @Schema(description = "Resource event", requiredMode = Schema.RequiredMode.REQUIRED)
    private ResourceEvent event;

    @Schema(description = "List of triggers associated with event", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UUID> triggerUuids;
}

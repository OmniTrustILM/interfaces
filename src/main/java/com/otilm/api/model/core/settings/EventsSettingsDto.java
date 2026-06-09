package com.otilm.api.model.core.settings;

import com.otilm.api.model.core.other.ResourceEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing settings of multiple events mapped by its enum")
public class EventsSettingsDto implements SettingsDto {

    @NotNull
    @Schema(description = "Map of triggers associated to event where key is resource event enum and value is list of trigger UUIDs", requiredMode = Schema.RequiredMode.REQUIRED, propertyNames = ResourceEvent.class)
    private Map<ResourceEvent, List<UUID>> eventsMapping = new EnumMap<>(ResourceEvent.class);
}

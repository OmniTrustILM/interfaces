package com.otilm.api.model.core.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashMap;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class KeyEventHistoryDto {
    @Schema(description = "UUID of the event", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "UUID of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyUuid;

    @Schema(description = "Event creation time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime created;

    @Schema(description = "Created By", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createdBy;

    @Schema(description = "Event type", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyEvent event;

    @Schema(description = "Event result", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyEventStatus status;

    @Schema(description = "Event message", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Additional information for the event")
    private HashMap<String, Object> additionalInformation;
}

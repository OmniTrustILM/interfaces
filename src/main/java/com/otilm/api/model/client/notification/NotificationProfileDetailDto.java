package com.otilm.api.model.client.notification;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.notification.RecipientType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class NotificationProfileDetailDto {

    @Schema(description = "UUID of the Notification profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Name of the Notification profile", examples = {"NotificationProfile1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the Notification profile", examples = {"Detail description of the notification profile"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Latest version of the Notification profile", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Recipient type of notifications produced by profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private RecipientType recipientType;

    @Schema(description = "Recipients info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<NameAndUuidDto> recipients;

    @Schema(description = "Notification instance info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto notificationInstance;

    @Schema(description = "Is notification profile sending internal notifications", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean internalNotification;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Frequency of repeated notification", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "duration", example = "P1DT12H")
    private Duration frequency;

    @Schema(description = "Maximum number of repetitions of same notification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer repetitions;
}

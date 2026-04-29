package com.otilm.api.model.client.notification;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class NotificationDto {
    @Schema(description = "Notification UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Notification message", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Notification message detail")
    private String detail;

    @Schema(description = "Notification read date")
    private Date readAt;

    @Schema(description = "Notification sent date", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date sentAt;

    @Schema(description = "Target object type")
    private Resource targetObjectType;

    @Schema(description = "Target object identification (UUID)")
    private List<String> targetObjectIdentification;

    public NotificationDto(UUID uuid, String message, String detail, Date readAt, Date sentAt, Resource targetObjectType, String targetObjectIdentification) {
        this.uuid = uuid;
        this.message = message;
        this.detail = detail;
        this.readAt = readAt;
        this.sentAt = sentAt;
        this.targetObjectType = targetObjectType;
        if (targetObjectIdentification != null) {
            this.targetObjectIdentification = List.of(targetObjectIdentification.split(","));
        }
    }
}

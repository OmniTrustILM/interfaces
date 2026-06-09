package com.otilm.api.model.client.notification;

import com.otilm.api.model.core.scheduler.PaginationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationResponseDto extends PaginationResponseDto {
    @Schema(
            description = "Notifications",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<NotificationDto> items;
}

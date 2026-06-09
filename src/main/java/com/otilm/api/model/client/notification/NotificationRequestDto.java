package com.otilm.api.model.client.notification;

import com.otilm.api.model.core.scheduler.PaginationRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springdoc.core.annotations.ParameterObject;

@EqualsAndHashCode(callSuper = true)
@Data
@ParameterObject
public class NotificationRequestDto extends PaginationRequestDto {
    @Schema(
            description = "Show only unread notifications",
            defaultValue = "false"
    )
    private boolean unread;
}

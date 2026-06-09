package com.otilm.api.model.client.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationProfileRequestDto extends NotificationProfileUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the Notification profile",
            examples = {"NotificationProfile1"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

}

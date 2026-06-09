package com.otilm.api.model.core.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class NotificationInstanceRequestDto extends NotificationInstanceUpdateRequestDto {

    @Schema(description = "Notification instance name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "UUID of Notification provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Notification instance Kind", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name)
                .append("connectorUuid", connectorUuid)
                .append("kind", kind)
                .toString();
    }
}

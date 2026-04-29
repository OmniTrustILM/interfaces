package com.otilm.api.model.connector.notification;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Data
public class NotificationProviderInstanceRequestDto {

    @Schema(description = "Notification instance name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Kind of Notification instance",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "List of Notification instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("kind", kind)
                .append("name", name)
                .append("attributes", attributes)
                .toString();
    }
}

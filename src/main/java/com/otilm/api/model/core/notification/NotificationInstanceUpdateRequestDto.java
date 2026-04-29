package com.otilm.api.model.core.notification;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class NotificationInstanceUpdateRequestDto {

    @Schema(description = "Notification instance description")
    private String description;

    @Schema(description = "List of Notification instance Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "List of attribute mappings between mapping attributes and (recipient) custom attributes")
    private List<AttributeMappingDto> attributeMappings;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("attributes", attributes)
                .append("attributeMappings", attributeMappings)
                .toString();
    }
}

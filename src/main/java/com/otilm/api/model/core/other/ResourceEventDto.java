package com.otilm.api.model.core.other;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class ResourceEventDto {

    @Schema(description = "Resource event code",
            examples = {"discoveryFinished"},
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = ResourceEvent.class)
    private ResourceEvent event;

    @Schema(description = "Resource of objects that are subject of event", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Resource producedResource;

    public ResourceEventDto() {
    }

    public ResourceEventDto(ResourceEvent event) {
        this.event = event;
        this.producedResource = event.getResource();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("event", event)
                .append("producedResource", producedResource)
                .toString();
    }
}

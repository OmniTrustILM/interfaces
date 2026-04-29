package com.otilm.api.model.connector.discovery;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class DiscoveryRequestDto {

    @Schema(description = "Name of the Discovery",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Discovery Kind",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Discovery Provider Attributes. Mandatory for creating new Discovery"
    )
    private List<RequestAttribute> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("kind", kind)
                .append("attributes", attributes)
                .toString();
    }
}

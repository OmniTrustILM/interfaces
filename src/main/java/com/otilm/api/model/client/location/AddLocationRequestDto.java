package com.otilm.api.model.client.location;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing Location registration request from clients
 */
@Getter
@Setter
public class AddLocationRequestDto {

    @Schema
            (description = "Location name",
            requiredMode = Schema.RequiredMode.REQUIRED
            )
    private String name;

    @Schema(
            description = "Location description"
    )
    private String description;

    @Schema(
            description = "List of Attributes to register Location",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(
            description = "Enabled flag - true = enabled; false = disabled",
            defaultValue = "false"
    )
    private Boolean enabled;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("description", description)
                .append("attributes", attributes)
                .append("enabled", enabled)
                .append("customAttributes", customAttributes)
                .toString();
    }

    public Boolean isEnabled() {
        return enabled;
    }
}

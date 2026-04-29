package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing RA profile registration request
 */
@Setter
@Getter
public class AddRaProfileRequestDto {

    @Schema(description = "RA Profile name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "RA Profile description")
    private String description;

    @Schema(description = "List of Attributes to create RA Profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "Enabled flag - true = enabled; false = disabled", defaultValue = "false")
    private Boolean enabled;


    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("description", description)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("enabled", enabled)
                .toString();
    }
}

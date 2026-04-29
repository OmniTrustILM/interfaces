package com.otilm.api.model.client.auth;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class RoleRequestDto {

    @Schema(description = "Name of the role")
    private String name;

    @Schema(description = "Description for the role")
    private String description;

    @Schema(description = "Role contact email")
    private String email;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("description", description)
                .append("email", email)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

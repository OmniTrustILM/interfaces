package com.otilm.api.model.core.auth;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class RoleDetailDto extends RoleDto {

    @Schema(description = "List of Users with the role", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UserDto> users;

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("users", users)
                .append("description", getDescription())
                .append("systemRole", getSystemRole())
                .append("customAttributes", customAttributes)
                .toString();
    }
}

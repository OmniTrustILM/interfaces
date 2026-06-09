package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class UserProfileDto {

    @Schema(description = "User details", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDto user;

    @Schema(description = "User Roles", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<NameAndUuidDto> roles;

    @Schema(description = "User Permissions", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubjectPermissionsDto permissions;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<NameAndUuidDto> getRoles() {
        return roles;
    }

    public void setRoles(List<NameAndUuidDto> roles) {
        this.roles = roles;
    }

    public SubjectPermissionsDto getPermissions() {
        return permissions;
    }

    public void setPermissions(SubjectPermissionsDto permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("user", user)
                .append("roles", roles)
                .append("permissions", permissions)
                .toString();
    }
}

package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfileDetailDto extends UserDetailDto {

    @Schema(description = "User permissions", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserProfilePermissionsDto permissions;

    public UserProfileDetailDto() {

    }

    public UserProfileDetailDto(UserDetailDto userDetailDto, UserProfilePermissionsDto permissions) {
        this.setUuid(userDetailDto.getUuid());
        this.setUsername(userDetailDto.getUsername());
        this.setFirstName(userDetailDto.getFirstName());
        this.setLastName(userDetailDto.getLastName());
        this.setEmail(userDetailDto.getEmail());
        this.setDescription(userDetailDto.getDescription());
        this.setGroups(userDetailDto.getGroups());
        this.setEnabled(userDetailDto.getEnabled());
        this.setSystemUser(userDetailDto.getSystemUser());
        this.setCertificate(userDetailDto.getCertificate());
        this.setRoles(userDetailDto.getRoles());
        this.setCustomAttributes(userDetailDto.getCustomAttributes());

        this.permissions = permissions;
    }
}

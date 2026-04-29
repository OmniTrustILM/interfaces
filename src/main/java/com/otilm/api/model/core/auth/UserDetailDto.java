package com.otilm.api.model.core.auth;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class UserDetailDto extends UserDto implements Loggable {

    @Schema(description = "User Certificate details")
    private UserCertificateDto certificate;

    @Schema(description = "Roles for the user", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RoleDto> roles;

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", getUuid())
                .append("username", getUsername())
                .append("firstName", getFirstName())
                .append("lastName", getLastName())
                .append("email", getEmail())
                .append("description", getDescription())
                .append("certificate", certificate)
                .append("enabled", getEnabled())
                .append("roles", roles)
                .append("customAttributes", customAttributes)
                .toString();
    }

    @Override
    public Serializable toLogData() {
        Map<String, Object> logData = new HashMap<>();
        logData.put("uuid", getUuid());
        logData.put("username", getUsername());
        logData.put("roles", roles.stream().map(NameAndUuidDto::getName).toList());
        return (Serializable) logData;
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of();
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of();
    }
}

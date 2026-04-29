package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserDto implements Loggable {

    @Schema(description = "UUID of the User", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"5b5f0784-2519-11ed-861d-0242ac120002"})
    private String uuid;

    @Schema(description = "Username of the user", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"user1"})
    private String username;

    @Schema(description = "First name of the user")
    private String firstName;

    @Schema(description = "Last name of the user")
    private String lastName;

    @Schema(description = "Email of the user")
    private String email;

    @Schema(description = "Description of the user")
    private String description;

    @Schema(description = "Groups of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<NameAndUuidDto> groups = new ArrayList<>();

    @Schema(description = "Status of the user. True = Enabled, False = Disabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "Is System user. True = Yes, False = No", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean systemUser;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("username", username)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("description", description)
                .append("groups", groups)
                .append("enabled", enabled)
                .append("systemUser", systemUser)
                .toString();
    }

    @Override
    public Serializable toLogData() {
        return new NameAndUuidDto(this.uuid, this.username);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(username);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(UUID.fromString(uuid));
    }
}

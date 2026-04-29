package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRequestDto {

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

    @Schema(description = "Groups of the user")
    private List<NameAndUuidDto> groups = new ArrayList<>();

    @Schema(description = "Status of the user. True = Enabled, False = Disabled")
    private Boolean enabled;

    @Schema(description = "UUID of the Certificate")
    private String certificateUuid;

    @Schema(description = "Fingerprint of the Certificate")
    private String certificateFingerprint;

    @Schema(description = "Is System User")
    private Boolean systemUser;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("username", username)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("description", description)
                .append("groups", groups)
                .append("certificateUuid", certificateUuid)
                .append("enabled", enabled)
                .append("certificateFingerprint", certificateFingerprint)
                .append("systemUser", systemUser)
                .toString();
    }
}

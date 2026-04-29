package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserUpdateRequestDto {

    @Schema(description = "First name of the user")
    private String firstName;

    @Schema(description = "Last name of the user")
    private String lastName;

    @Schema(description = "Email of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Description of the user")
    private String description;

    @Schema(description = "Groups of the user")
    private List<NameAndUuidDto> groups = new ArrayList<>();

    @Schema(description = "UUID of the Certificate")
    private String certificateUuid;

    @Schema(description = "Fingerprint of the Certificate")
    private String certificateFingerprint;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("description", description)
                .append("groups", groups)
                .append("certificateUuid", certificateUuid)
                .append("certificateFingerprint", certificateFingerprint)
                .toString();
    }
}

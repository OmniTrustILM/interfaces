package com.otilm.api.model.client.auth;

import com.otilm.api.model.common.Named;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserRequestDto extends UpdateUserRequestDto implements Named {

    @Schema(description = "Username of the user", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"user1"})
    private String username;

    @Schema(description = "Status of the user. True = Enabled, False = Disabled")
    private Boolean enabled;

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("username", username)
                .append("description", getDescription())
                .append("groupUuids", getGroupUuids())
                .append("firstName", getFirstName())
                .append("lastName", getLastName())
                .append("email", getEmail())
                .append("certificateUuid", getCertificateUuid())
                .append("enabled", enabled)
                .append("certificateData", getCertificateData())
                .append("customAttributes", getCustomAttributes())
                .append("certificateCustomAttributes", getCertificateCustomAttributes())
                .toString();
    }
}

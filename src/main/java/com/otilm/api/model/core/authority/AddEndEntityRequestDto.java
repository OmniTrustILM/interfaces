package com.otilm.api.model.core.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class representing a request to add a new End Entity
 */
public class AddEndEntityRequestDto extends BaseEndEntityRequestDto {

    @Schema(description = "End Entity name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("raProfile", raProfile)
                .append("email", email)
                .append("extensionData", extensionData)
                .append("password", "masked")
                .append("subjectAltName", subjectAltName)
                .append("subjectDN", subjectDN)
                .append("username", username)
                .toString();
    }
}


package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthenticationResponseDto {

    @Schema(description = "Is User Authenticated", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean authenticated;

    @Schema(description = "User profile data")
    private UserProfileDto data;

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public UserProfileDto getData() {
        return data;
    }

    public void setData(UserProfileDto data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("authenticated", authenticated)
                .append("data", data)
                .toString();
    }
}

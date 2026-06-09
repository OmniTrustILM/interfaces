package com.otilm.api.model.client.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class representing a request to revoke and delete End Entity
 */
public class RevokeAndDeleteRequestDto {
	
	@Schema(
            description = "Reason for the revocation",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer reason;
	
	@Schema(
            description = "Name of the user",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("reason", reason)
                .append("username", username)
                .toString();
    }
}

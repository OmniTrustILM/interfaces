package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@Schema(description = "Login provider information")
public class LoginProviderDto {
    @Schema(description = "Provider name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Provider login URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginUrl;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("loginUrl", loginUrl)
                .toString();
    }
}

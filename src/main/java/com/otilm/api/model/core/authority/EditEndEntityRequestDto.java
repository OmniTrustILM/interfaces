package com.otilm.api.model.core.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class representing a request to edit a End Entity
 */
public class EditEndEntityRequestDto extends BaseEndEntityRequestDto {

    @Schema(description = "End Entity Subject domain name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected EndEntityStatus status;

    public EndEntityStatus getStatus() {
        return status;
    }

    public void setStatus(EndEntityStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("raProfile", raProfile)
                .append("email", email)
                .append("extensionData", extensionData)
                .append("password", "masked")
                .append("status", status)
                .append("subjectAltName", subjectAltName)
                .append("subjectDN", subjectDN)
                .toString();
    }
}

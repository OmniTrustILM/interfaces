package com.otilm.api.model.core.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * EndEntityDTO
 */
public class EndEntityDto {

    @Schema(description = "End Entity subject domain name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectDN;

    @Schema(description = "End Entity email")
    private String email;

    @Schema(description = "End Entity extension data")
    private List<EndEntityExtendedInfoDto> extensionData;

    @Schema(description = "End Entity Subject alternative name")
    private String subjectAltName;

    @Schema(description = "End Entity Subject domain name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private EndEntityStatus status;

    @Schema(description = "End Entity name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EndEntityDto addExtensionDataItem(EndEntityExtendedInfoDto extensionDataItem) {
        if (this.extensionData == null) {
            this.extensionData = new ArrayList<EndEntityExtendedInfoDto>();
        }
        this.extensionData.add(extensionDataItem);
        return this;
    }

    public List<EndEntityExtendedInfoDto> getExtensionData() {
        return extensionData;
    }

    public void setExtensionData(List<EndEntityExtendedInfoDto> extensionData) {
        this.extensionData = extensionData;
    }

    public String getSubjectAltName() {
        return subjectAltName;
    }

    public void setSubjectAltName(String subjectAltName) {
        this.subjectAltName = subjectAltName;
    }

    public EndEntityStatus getStatus() {
        return status;
    }

    public void setStatus(EndEntityStatus status) {
        this.status = status;
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
                .append("subjectDN", subjectDN)
                .append("email", email)
                .append("extensionData", extensionData)
                .append("subjectAltName", subjectAltName)
                .append("endEntityStatus", status)
                .append("username", username)
                .toString();
    }
}

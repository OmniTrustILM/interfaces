package com.otilm.api.model.client.authority;

import com.otilm.api.model.core.authority.EndEntityExtendedInfoDto;
import com.otilm.api.model.core.raprofile.RaProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Class representing a base End Entity request
 */
public class ClientBaseEndEntityRequestDto {

    @Schema(description = "RA profile related to End Entity",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected RaProfileDto raProfile;

    @Schema(description = "End Entity email")
    protected String email;

    @Schema(description = "End Entity extension data")
    protected List<EndEntityExtendedInfoDto> extensionData;

    @Schema(description = "End Entity password",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected String password;

    @Schema(description = "End Entity Subject alternative name")
    protected String subjectAltName;

    @Schema(description = "End Entity subject domain name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected String subjectDN;

    public RaProfileDto getRaProfile() {
        return raProfile;
    }

    public void setRaProfile(RaProfileDto raProfile) {
        this.raProfile = raProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<EndEntityExtendedInfoDto> getExtensionData() {
        return extensionData;
    }

    public void setExtensionData(List<EndEntityExtendedInfoDto> extensionData) {
        this.extensionData = extensionData;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubjectAltName() {
        return subjectAltName;
    }

    public void setSubjectAltName(String subjectAltName) {
        this.subjectAltName = subjectAltName;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }
}


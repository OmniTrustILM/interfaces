package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;

public class CertificateUpdateGroupDto {

    @Schema(description = "Group UUID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String groupUuid;

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public CertificateUpdateGroupDto() {
    }
}

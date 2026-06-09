package com.otilm.api.model.client.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceRulesListRequestDto {
    @Schema(description = "UUID of the Compliance Provider. If not provided, rules from all the Providers is returned",
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120003"},
            defaultValue = "null")
    private String uuid;

    @Schema(description = "Kind of the Compliance Provider. If not provided, rules from all the kinds is returned",
            examples = {"Kind1"},
            defaultValue = "null")
    private String kind;

    @Schema(description = "Type of the certificate to query for the rules",
            examples = {"x509"},
            defaultValue = "null")
    private List<String> certificateType;

    //Default getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<String> getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(List<String> certificateType) {
        this.certificateType = certificateType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("kind", kind)
                .append("certificateType", certificateType)
                .toString();
    }
}

package com.otilm.api.model.core.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceConnectorAndGroupsDto {
    @Schema(description = "Name of the Compliance Provider")
    private String connectorName;

    @Schema(description = "UUID of the Compliance Provider")
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider")
    private String kind;

    @Schema(description = "Groups associated")
    private List<ComplianceGroupsDto> groups;

    //Default getters and setters

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<ComplianceGroupsDto> getGroups() {
        return groups;
    }

    public void setGroups(List<ComplianceGroupsDto> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", connectorUuid)
                .append("name", connectorName)
                .append("groups", groups)
                .append("kind", kind)
                .toString();
    }
}

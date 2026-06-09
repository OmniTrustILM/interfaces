package com.otilm.api.model.client.compliance;

import com.otilm.api.model.connector.compliance.ComplianceGroupsResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceGroupsListResponseDto {
    @Schema(description = "Name of the Compliance Provider",
            examples = {"Provider1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    @Schema(description = "UUID of the Compliance Provider",
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120003"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider",
            examples = {"Kind1"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Groups from Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceGroupsResponseDto> groups;


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

    public List<ComplianceGroupsResponseDto> getGroups() {
        return groups;
    }

    public void setGroups(List<ComplianceGroupsResponseDto> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("connectorName", connectorName)
                .append("connectorUuid", connectorUuid)
                .append("kind",kind)
                .append("groups", groups)
                .toString();
    }
}

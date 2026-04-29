package com.otilm.api.model.core.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@NoArgsConstructor
public class ComplianceProviderSummaryDto {
    @Schema(description = "Name of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    @Schema(description = "Number of rules for the Provider")
    private Integer numberOfRules;

    @Schema(description = "Number of groups for the Provider")
    private Integer numberOfGroups;

    public ComplianceProviderSummaryDto(String connectorName) {
        this.connectorName = connectorName;
        this.numberOfRules = 0;
        this.numberOfGroups = 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", connectorName)
                .append("rules", numberOfRules)
                .append("groups", numberOfGroups)
                .toString();
    }
}

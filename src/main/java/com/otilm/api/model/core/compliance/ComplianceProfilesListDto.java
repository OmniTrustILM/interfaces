package com.otilm.api.model.core.compliance;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceProfilesListDto extends NameAndUuidDto {

    @Schema(description = "Compliance Profile description")
    private String description;

    @Schema(description = "Rules summary", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceProviderSummaryDto> rules;

    //Default getters and setters


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ComplianceProviderSummaryDto> getRules() {
        return rules;
    }

    public void setRules(List<ComplianceProviderSummaryDto> rules) {
        this.rules = rules;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("rules", rules)
                .toString();
    }
}

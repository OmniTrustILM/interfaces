package com.otilm.api.model.core.compliance;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ComplianceProfileDto extends NameAndUuidDto {

    @Schema(description = "Description of the Compliance Profile")
    private String description;

    @Schema(description = "List of rules", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceConnectorAndRulesDto> rules;

    @Schema(description = "List of groups", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceConnectorAndGroupsDto> groups;

    @Schema(description = "List of associated RA Profiles")
    private List<SimplifiedRaProfileDto> raProfiles;

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    //Default getters and setters

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("rules", rules)
                .append("raProfiles", raProfiles)
                .append("groups", groups)
                .append("description", description)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

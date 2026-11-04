package com.otilm.api.model.core.compliance;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class ComplianceProfileDto extends NameAndUuidDto implements ObjectActionsAware {

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

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

    // Default getters and setters

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
                .append("allowedActions", allowedActions)
                .toString();
    }
}

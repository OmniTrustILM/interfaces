package com.otilm.api.model.client.compliance;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ComplianceProfileRequestDto {
    @Schema(description = "Name of the Compliance Profile", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Profile 1"})
    private String name;

    @Schema(description = "Description of the Compliance Profile", examples = {"Profile 1"})
    private String description;

    @Schema(description = "Rules to be associated with the Compliance Profile. Profiles can be created without rules and can be added later")
    private List<ComplianceProfileRulesRequestDto> rules;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("description", description)
                .append("rules", rules)
                .append("customAttributes", customAttributes)
                .toString();
    }
}

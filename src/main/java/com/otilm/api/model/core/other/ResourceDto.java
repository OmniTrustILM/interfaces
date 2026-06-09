package com.otilm.api.model.core.other;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class ResourceDto {

    @Schema(description = "Resource code",
            examples = {"certificates"},
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = Resource.class)
    private Resource resource;

    @Schema(description = "If resource has Object access permissions. True = Yes, False = No", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean hasObjectAccess;

    @Schema(description = "Support assigning custom attributes to resource objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasCustomAttributes;

    @Schema(description = "Support assigning groups to resource objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasGroups;

    @Schema(description = "Support assigning owner to resource objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasOwner;

    @Schema(description = "Has events that can be used in triggers", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasEvents;

    @Schema(description = "Has rule evaluator that can evaluate conditions and actions", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasRuleEvaluator;

    @Schema(description = "Is resource subject of compliance check and can compliance be run on objects of this resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean complianceSubject;

    @Schema(description = "Can compliance profiles be assigned to objects of this resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasComplianceProfiles;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("resource", resource)
                .append("hasObjectAccess", hasObjectAccess)
                .append("hasCustomAttributes", hasCustomAttributes)
                .append("hasGroups", hasGroups)
                .append("hasOwner", hasOwner)
                .append("hasEvents", hasEvents)
                .append("hasRuleEvaluator", hasRuleEvaluator)
                .append("complianceSubject", complianceSubject)
                .append("hasComplianceProfiles", hasComplianceProfiles)
                .toString();
    }
}

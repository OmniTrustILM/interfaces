package com.otilm.api.model.core.compliance.v2;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.ComplianceRuleAvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceGroupDto {
    @Schema(description = "Compliance group UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Compliance group name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the compliance group", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"Sample group description"})
    private String description;

    @Schema(description = "Availability status of the compliance group", requiredMode = Schema.RequiredMode.REQUIRED, examples = {ComplianceRuleAvailabilityStatus.Codes.AVAILABLE})
    private ComplianceRuleAvailabilityStatus availabilityStatus;

    @Schema(description = "Reason why compliance group availability status is `Updated`", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String updatedReason;

    @Schema(description = "Resource of the rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {Resource.Codes.CERTIFICATE})
    private Resource resource;

}

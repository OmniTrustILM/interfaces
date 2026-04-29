package com.otilm.api.model.core.compliance.v2;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.ComplianceRuleAvailabilityStatus;
import com.otilm.api.model.core.workflows.ConditionItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceRuleDto {
    @Schema(description = "Compliance rule UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Compliance rule name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the compliance rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"Sample rule description"})
    private String description;

    @Schema(description = "UUID of the group to which the rule belongs to", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID groupUuid;

    @Schema(description = "Availability status of the compliance rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {ComplianceRuleAvailabilityStatus.Codes.AVAILABLE})
    private ComplianceRuleAvailabilityStatus availabilityStatus;

    @Schema(description = "Reason why compliance rule availability status is `Updated`", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String updatedReason;

    @Schema(description = "Resource of the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {Resource.Codes.CERTIFICATE})
    private Resource resource;

    @Schema(description = "Type of the resource object", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"X.509"})
    private String type;

    @Schema(description = "Format of the resource object", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"pkcs7"})
    private String format;

    @Schema(description = "Attributes of the rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of the compliance internal rule condition items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConditionItemDto> conditionItems;

}

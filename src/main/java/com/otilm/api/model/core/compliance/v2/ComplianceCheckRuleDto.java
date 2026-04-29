package com.otilm.api.model.core.compliance.v2;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.ComplianceRuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceCheckRuleDto implements Serializable {
    @Schema(description = "Compliance rule UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Compliance rule name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the compliance rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"Sample rule description"})
    private String description;

    @Schema(description = "Result status of rule compliance check", requiredMode = Schema.RequiredMode.REQUIRED, examples = {ComplianceRuleStatus.Codes.OK})
    private ComplianceRuleStatus status;

    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID connectorUuid;

    @Schema(description = "Name of the Compliance Provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorName;

    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kind;

    @Schema(description = "Resource of the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {Resource.Codes.CERTIFICATE})
    private Resource resource;

    @Schema(description = "Attributes of the rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes;

}

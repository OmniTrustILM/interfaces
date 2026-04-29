package com.otilm.api.model.core.compliance.v2;

import com.otilm.api.model.client.attribute.ResponseAttribute;
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
@Schema(name = "ComplianceProfileDtoV2")
public class ComplianceProfileDto {
    @Schema(description = "Compliance profile UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Compliance profile name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the Compliance Profile")
    private String description;

    @Schema(description = "List of internal rules", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceRuleDto> internalRules = new ArrayList<>();

    @Schema(description = "List of groups", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProviderComplianceRulesDto> providerRules = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}

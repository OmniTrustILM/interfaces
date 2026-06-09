package com.otilm.api.model.client.compliance.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class ComplianceProfileUpdateRequestDto {
    @Schema(description = "Description of the Compliance Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"Profile 1"})
    private String description;

    @Schema(description = "UUIDs of internal rules to be associated with the Compliance Profile. Profiles can be created without rules and can be added later.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<UUID> internalRules = new HashSet<>();

    @Valid
    @Schema(description = "Provider rules to be associated with the Compliance Profile. Profiles can be created without rules and can be added later.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ProviderComplianceRulesRequestDto> providerRules = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();

}

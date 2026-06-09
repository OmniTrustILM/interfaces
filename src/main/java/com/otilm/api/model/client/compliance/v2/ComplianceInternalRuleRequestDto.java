package com.otilm.api.model.client.compliance.v2;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.logging.Loggable;
import com.otilm.api.model.core.workflows.ConditionItemRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceInternalRuleRequestDto implements Loggable {

    @NotNull
    @NotBlank
    @Schema(description = "Name of the compliance internal rule", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description of the compliance internal rule")
    private String description;

    @NotNull
    @Schema(description = "Resource associated with the compliance internal rule", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @NotNull
    @NotEmpty
    @Schema(description = "List of the condition items to add to compliance internal rule", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ConditionItemRequestDto> conditionItems = new ArrayList<>();

    @JsonIgnore
    @AssertTrue(message = "Resource must be a valid compliance subject")
    public boolean isResourceValid() {
        return resource != null && resource.complianceSubject();
    }

    @Override
    public Serializable toLogData() {
        return null;
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(name);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of();
    }
}

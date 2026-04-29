package com.otilm.api.model.core.compliance.v2;

import com.otilm.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ComplianceCheckResultDto implements Serializable {

    @Schema(description = "Overall compliance result status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ComplianceStatus status;

    @Schema(description = "Date of the most recent compliance check", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime timestamp;

    @Schema(description = "Overall compliance check result message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;

    @Schema(description = "Compliance rules that didn't pass compliance check", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceCheckRuleDto> failedRules = new ArrayList<>();

}

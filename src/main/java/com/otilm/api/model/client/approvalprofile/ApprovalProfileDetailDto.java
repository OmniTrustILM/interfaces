package com.otilm.api.model.client.approvalprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalProfileDetailDto extends ApprovalProfileDto {

    @Schema(description = "List of Approval steps for the Approval profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ApprovalStepDto> approvalSteps;


}

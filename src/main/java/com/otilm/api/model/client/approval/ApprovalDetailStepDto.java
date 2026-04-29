package com.otilm.api.model.client.approval;

import com.otilm.api.model.client.approvalprofile.ApprovalStepDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalDetailStepDto extends ApprovalStepDto {

    @Schema(
            description = "List of the approval recipient related for this step",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ApprovalStepRecipientDto> approvalStepRecipients;

    public ApprovalDetailStepDto(final ApprovalStepDto approvalStepDto) {
        super(approvalStepDto.getUserUuid(), approvalStepDto.getRoleUuid(), approvalStepDto.getGroupUuid(), approvalStepDto.getDescription(), approvalStepDto.getOrder(), approvalStepDto.getRequiredApprovals(), approvalStepDto.getUuid(), approvalStepDto.getUsername(), approvalStepDto.getRoleName(), approvalStepDto.getGroupName());
    }
}

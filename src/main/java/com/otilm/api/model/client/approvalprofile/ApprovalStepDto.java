package com.otilm.api.model.client.approvalprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStepDto extends ApprovalStepRequestDto {
    @Schema(
            description = "UUID of the Approval step",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID uuid;

    @Schema(description = "Username of the responsible user to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    @Schema(description = "Name of the responsible role of the users to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleName;

    @Schema(description = "Name of the responsible group of the users to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String groupName;

    public ApprovalStepDto(UUID userUuid, UUID roleUuid, UUID groupUuid, String description, int order, int requiredApprovals, UUID approvalStepUuid, String username, String roleName, String groupName) {
        super(userUuid, roleUuid, groupUuid, description, order, requiredApprovals);
        this.uuid = approvalStepUuid;
        this.username = username;
        this.roleName = roleName;
        this.groupName = groupName;
    }
}

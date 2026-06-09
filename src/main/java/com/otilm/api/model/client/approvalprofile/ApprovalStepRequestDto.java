package com.otilm.api.model.client.approvalprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStepRequestDto {

    @Schema(description = "UUID of the responsible user to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID userUuid;

    @Schema(description = "UUID of the responsible role of the users to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID roleUuid;

    @Schema(description = "UUID of the responsible group of the users to approve action in approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID groupUuid;

    @Schema(description = "Description of the approval step",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Order of the position in the approval steps flow",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int order;

    @Schema(description = "Count of the required approvals for the approval step, by default there is 1 approval needed.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int requiredApprovals;

}

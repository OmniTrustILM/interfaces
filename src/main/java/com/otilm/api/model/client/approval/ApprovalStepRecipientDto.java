package com.otilm.api.model.client.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class ApprovalStepRecipientDto {

    @Schema(
            description = "UUID of the approval recipient",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String approvalRecipientUuid;

    @Schema(
            description = "UUID of the recipient user",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String userUuid;

    @Schema(
            description = "Username of the recipient",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String username;

    @Schema(
            description = "Creating date of the approval recipient",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Date createdAt;

    @Schema(
            description = "Resolution date of the approval recipient",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Date closedAt;

    @Schema(
            description = "Status of the approval recipient",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ApprovalStatusEnum status;

    @Schema(
            description = "Comment of the approval recipient",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String comment;


}

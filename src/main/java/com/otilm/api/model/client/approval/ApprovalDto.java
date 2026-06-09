package com.otilm.api.model.client.approval;


import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApprovalDto {

    @Schema(
            description = "UUID of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String approvalUuid;

    @Schema(
            description = "UUID of the user that requested approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String creatorUuid;

    @Schema(
            description = "Username of the user that requested approval",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String creatorUsername;

    @Schema(
            description = "Version of the Approval profile",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int version;

    @Schema(
            description = "Creation date of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Date createdAt;

    @Schema(
            description = "Expiry date of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Date expiryAt;

    @Schema(
            description = "Date of resolution of the Approval",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Date closedAt;

    @Schema(
            description = "Status of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ApprovalStatusEnum status;

    @Schema(
            description = "Resource of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

    @Schema(
            description = "Resource action of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resourceAction;

    @Schema(
            description = "UUID of the target object of the Approval",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String objectUuid;

    @Schema(
            description = "Name of the Approval profile",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String approvalProfileName;

    @Schema(
            description = "UUID of the Approval profile",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String approvalProfileUuid;

    public ApprovalDto(final ApprovalDto approvalDto) {
        this.approvalProfileName = approvalDto.getApprovalProfileName();
        this.approvalProfileUuid = approvalDto.getApprovalProfileUuid();
        this.approvalUuid = approvalDto.getApprovalUuid();
        this.creatorUuid = approvalDto.getCreatorUuid();
        this.creatorUsername = approvalDto.getCreatorUsername();
        this.closedAt = approvalDto.getClosedAt();
        this.createdAt = approvalDto.getCreatedAt();
        this.expiryAt = approvalDto.getExpiryAt();
        this.objectUuid = approvalDto.getObjectUuid();
        this.status = approvalDto.getStatus();
        this.version = approvalDto.getVersion();
        this.resource = approvalDto.getResource();
        this.resourceAction = approvalDto.getResourceAction();
    }
}

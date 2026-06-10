package com.otilm.api.model.common.events.data;

import com.otilm.api.model.client.approval.ApprovalStatusEnum;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.notification.RecipientType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApprovalEventData implements EventData {
    @Schema(description = "Approval UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID approvalUuid;

    @Schema(description = "Approval profile UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID approvalProfileUuid;

    @Schema(description = "Approval profile name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String approvalProfileName;

    @Schema(description = "Version of the Approval profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Status of the Approval", requiredMode = Schema.RequiredMode.REQUIRED)
    private ApprovalStatusEnum status;

    @Schema(description = "Approval expiry date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date expiryAt;

    @Schema(description = "Date of resolution of the Approval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date closedAt;

    @Schema(description = "Approval associated resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "Approval associated resource action", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resourceAction;

    @Schema(description = "Approval associated object UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID objectUuid;

    @Schema(description = "UUID of the user that created approval", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID creatorUuid;

    @Schema(description = "Username of the user that created approval", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creatorUsername;

    @Schema(description = "Approval Recipient type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private RecipientType recipientType;

    @Schema(description = "Approval recipient UUID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID recipientUuid;



}

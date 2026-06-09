package com.otilm.api.model.client.certificate;

import com.otilm.api.model.core.certificate.BulkOperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class BulkOperationResponse {
    @Schema(description = "Status of the operation", requiredMode = Schema.RequiredMode.REQUIRED)
    private BulkOperationStatus status;

    @Schema(description = "Number of items failed", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long failedItem;

    @Schema(description = "Message for the action", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;


    public BulkOperationStatus getStatus() {
        return status;
    }

    public void setStatus(BulkOperationStatus status) {
        this.status = status;
    }

    public Long getFailedItem() {
        return failedItem;
    }

    public void setFailedItem(Long failedItem) {
        this.failedItem = failedItem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.otilm.api.model.client.approval;

import com.otilm.api.model.common.enums.IPlatformEnum;

public enum ApprovalStatusEnum implements IPlatformEnum {

    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    EXPIRED("Expired");

    private String code;

    ApprovalStatusEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return "";
    }
}

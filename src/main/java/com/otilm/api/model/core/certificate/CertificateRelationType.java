package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum CertificateRelationType implements IPlatformEnum {

    RENEWAL("renewal", "Renewal", "Successor certificate is renewal of predecessor certificate"),
    REKEY("rekey", "Rekey", "Successor certificate is rekey of predecessor certificate"),
    REPLACEMENT("replacement", "Replacement", "Successor certificate is replacement of predecessor certificate"),
    PENDING("pending", "Pending", "The relation type is to be decided after successor certificate is issued")
    ;

    private final String code;
    private final String label;
    private final String description;

    CertificateRelationType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }


    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
